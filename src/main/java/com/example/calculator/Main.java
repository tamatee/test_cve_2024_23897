package com.example.calculator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Calculator calculator = new Calculator();
        
        System.out.println("Simple Calculator");
        System.out.println("----------------");
        
        try {
            System.out.print("Enter first number: ");
            int num1 = scanner.nextInt();
            
            System.out.print("Enter second number: ");
            int num2 = scanner.nextInt();
            
            System.out.println("\nResults:");
            System.out.println("Addition: " + calculator.add(num1, num2));
            System.out.println("Subtraction: " + calculator.subtract(num1, num2));
            System.out.println("Multiplication: " + calculator.multiply(num1, num2));
            
            try {
                System.out.println("Division: " + calculator.divide(num1, num2));
            } catch (ArithmeticException e) {
                System.out.println("Division: Error - " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("Error: Invalid input. Please enter valid integers.");
        } finally {
            scanner.close();
        }
    }
}