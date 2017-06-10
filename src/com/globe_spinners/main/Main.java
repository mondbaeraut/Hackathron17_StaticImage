package com.globe_spinners.main;

import javax.swing.JOptionPane;

public class Main {
	
	public static void main(String[] args) {
		Thread t = new Thread() {
			public void run() {
				ImageController ic = new ImageController();
				ic.init();
				
				while (true) {
					ic.run();
				}
			}
		};
		t.start();
	}
}
