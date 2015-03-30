package com.iliakplv.java;

import java.util.Random;

public class QuickSort {

	public static void main(String[] args) {

		final int[] arr = new int[10];
		final Random rnd = new Random();
		for (int i = 0; i < arr.length; i++) {
			arr[i] = 10 + rnd.nextInt(100 - 10 + 1);
		}
		printArray(arr);
		quickSort(arr, 0, arr.length - 1);
	}

	private static void quickSort(int[] arr, int start, int end) {
		if (end > start) {
			final int part = partition(arr, start, end);
			quickSort(arr, start, part - 1);
			quickSort(arr, part + 1, end);
		}
	}

	private static int partition(int[] arr, int start, int end) {
		int partition = start;
		for (int i = start; i <= end; i++) {
			if (arr[i] <= arr[end]) {
				swap(arr, i, partition);
				partition++;
			}
		}
		printArray(arr);
		return partition - 1;
	}

	private static void swap(int[] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}

	private static void printArray(int[] arr) {
		System.out.print('\n');

		for (int i : arr) {
			System.out.print(i + " ");
		}
	}
}
