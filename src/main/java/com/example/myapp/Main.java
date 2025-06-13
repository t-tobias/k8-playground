
package com.example.myapp;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Maven + Kubernetes project!");
        
        try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
