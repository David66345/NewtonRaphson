/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package newtonraphson;

import java.util.Scanner;

/**
 *
 * @author david
 */
public class NewtonRaphson {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        NewtonRaphson nr = new NewtonRaphson();
        
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese la ecuación:");
        System.out.print("F(x) = ");
        String ecuacion = scanner.nextLine();

        System.out.println("Ingrese el punto de partida:");
        double puntoPartida = scanner.nextDouble();

        System.out.println("Ingrese la tolerancia:");
        double tolerancia = scanner.nextDouble();

        // Calcula la derivada de la ecuación
        String derivada = nr.calcularDerivada(ecuacion);
        System.out.println("La derivada es: " + derivada);

        // Calcula los resultados usando el método de Newton-Raphson
        double[] resultados = nr.calcularNewtonRaphson(ecuacion, derivada, puntoPartida, tolerancia);
        if (resultados.length == 0) {
            System.out.println("No se encontraron soluciones.");
        } else {
            System.out.println("La solución más aproximada encontrada es: " + resultados[resultados.length - 1]);
        }
    }

    public String calcularDerivada(String ecuacion) {
        String[] terminos = ecuacion.split("(?=\\+|\\-)");
        
        double variable = 0;
        double coeficiente = 0;
        double exponente = 0;

        String derivada = "";
        for(int i = 0; i < terminos.length; i++){
            
            if(terminos[i].contains("x")){
                
                if(terminos[i].contains("^") || terminos[i].contains("*")){
                    String[] monomio = terminos[i].split("\\*");
                    String[] potencias = monomio[1].split("\\^");
                    
                    if(monomio[0].startsWith("x")){
                        coeficiente = 1.0;
                        exponente = Double.parseDouble(potencias[1]);
                    } else if(monomio[1].contains("^")){
                        coeficiente = Double.parseDouble(monomio[0]);
                        exponente = Double.parseDouble(potencias[1]);
                    } else {
                        coeficiente = Double.parseDouble(monomio[0]);
                        exponente = 1.0;
                    }
                    
                    coeficiente = coeficiente*exponente;
                    exponente = exponente - 1.0;
                    
                    // Agregar términos a la derivada, incluyendo el manejo de "+".
                    if (!derivada.isEmpty() && coeficiente > 0) {
                        derivada += "+";
                    }
                    
                    if(exponente == 0.0){
                        derivada += String.valueOf(coeficiente);
                    } else if(exponente == 1.0){
                        derivada += coeficiente+"*x";
                    }else{
                        derivada += coeficiente+"*x^"+exponente;
                    }
                }
            } else {
                terminos[i] = "";
            }
        }
        
        return derivada;
    }

    public double[] calcularNewtonRaphson(String ecuacion, String derivada, double puntoPartida, double tolerancia) {
        double x0 = puntoPartida;
        double x1 = 0;

        // Inicializa una lista para almacenar los resultados
        double[] resultados = new double[100]; // Suponiendo que habrá un máximo de 100 resultados
        int contadorResultados = 0;

        int maxIteraciones = 1000;
        int iteracionesSinMejora = 0;

        for (int i = 0; i < maxIteraciones && iteracionesSinMejora < 10; i++) {
            double fx0 = evaluar(ecuacion, x0);
            double dfx0 = evaluar(derivada, x0);

            if (Math.abs(dfx0) < 1E-7) {
                dfx0 = 0.0000001; // Ajuste para evitar división por cero
            }

            x1 = x0 - fx0 / dfx0;

            resultados[contadorResultados++] = x1; // Guarda el resultado de la iteración

            if (Math.abs(x1 - x0) < tolerancia) {
                iteracionesSinMejora++;
            } else {
                iteracionesSinMejora = 0; // Reset si hay una mejora significativa
            }
            
            if (Math.abs(x1 - x0) < tolerancia) {
                break; // Detiene el bucle si se alcanza la tolerancia
            }

            // Actualiza x0 para la próxima iteración
            x0 = x1;

            // Imprime los valores de la iteración actual
            System.out.println("Iteración " + (i + 1) + ": x = " + x1 + ", f(x) = " + fx0);
            
            if(resultados[1] == resultados[3] && contadorResultados == 4){
                break;
            }
        }

        // Copia los resultados a un nuevo array con el tamaño exacto
        double[] resultadosFinales = new double[contadorResultados];
        System.arraycopy(resultados, 0, resultadosFinales, 0, contadorResultados);

        return resultadosFinales;
    }

    
    public double evaluar(String expresion, double x) {
        // Reemplaza 'x' con el valor dado
        expresion = expresion.replaceAll("x", String.valueOf(x));

        // Evalúa la expresión y devuelve el resultado
        return evaluarExpresion(expresion);
    }

    public double evaluarExpresion(String expresion) {
        try {
            // Separa la expresión en términos individuales
            String[] terminos = expresion.split("(?=\\+|\\-)");

            // Inicializa el resultado
            double resultado = 0;

            // Calcula el resultado sumando los términos
            for (String termino : terminos) {
                termino = termino.trim(); // Elimina espacios en blanco que puedan causar problemas
                if (!termino.isEmpty()) { // Asegúrate de que el término no esté vacío
                    if (termino.contains("^")) {
                        String[] partes = termino.split("\\^");
                        double base = 1.0, exponente = 1.0;
                        if (partes.length > 1) {
                            exponente = Double.parseDouble(partes[1]);
                            String[] baseParts = partes[0].split("\\*");
                            if (baseParts.length > 0) {
                                base = Double.parseDouble(baseParts[baseParts.length - 1]);
                            }
                        }
                        double coeficiente = termino.contains("*") ? Double.parseDouble(termino.split("\\*")[0]) : 1.0;
                        resultado += coeficiente * Math.pow(base, exponente);
                    } else if (termino.contains("*")) {
                        String[] factores = termino.split("\\*");
                        if (factores.length == 2) { // Asegúrate de que hay dos factores antes de proceder
                            double coeficiente = Double.parseDouble(factores[0]);
                            double base = Double.parseDouble(factores[1]);
                            resultado += coeficiente * base;
                        }
                    } else {
                        resultado += Double.parseDouble(termino);
                    }
                }
            }

            return resultado;
        } catch (NumberFormatException e) {
            System.out.println("Error al evaluar la expresión: " + expresion);
            return Double.NaN; // Retorna NaN (Not a Number) en caso de error
        }
    }

}
