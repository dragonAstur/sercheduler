package com.uniovi.sercheduler.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeStringArray {
  // Size of the array, can be set to a default or loaded from a configuration
  private static final int DEFAULT_SIZE = 10;
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private String[] array;
  private int currentIndex = 0; // To track the next available position

  // Private constructor to prevent instantiation from outside
  private ThreadSafeStringArray() {
    this.array = new String[DEFAULT_SIZE];
  }

  // Static method to get the instance of the class
  public static ThreadSafeStringArray getInstance() {
    return InstanceHolder.instance;
  }

  // Method to add a value to the array
  public void setValue(String value) {
    lock.writeLock().lock();
    try {
      if (currentIndex >= array.length) {
        // Array is full, you might want to handle this case
        // e.g., throw an exception or resize the array
      } else {
        array[currentIndex] = value;
        currentIndex++; // Increment the index for the next insertion
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  public String getValue(int index) {
    lock.readLock().lock();
    try {
      return array[index];
    } finally {
      lock.readLock().unlock();
    }
  }

  public String[] getArray() {
    lock.readLock().lock();
    try {
      return Arrays.copyOf(array, array.length);
    } finally {
      lock.readLock().unlock();
    }
  }

  // Method to count the occurrences of each string in the array
  public Map<String, Integer> countOccurrences() {
    lock.readLock().lock();
    try {
      Map<String, Integer> countMap = new HashMap<>();
      for (String s : array) {
        if (s != null) {
          countMap.put(s, countMap.getOrDefault(s, 0) + 1);
        }
      }
      return countMap;
    } finally {
      lock.readLock().unlock();
    }
  }

  public void recreateArray(int newSize) {
    lock.writeLock().lock();
    try {
      array = new String[newSize];
      currentIndex = 0;
    } finally {
      lock.writeLock().unlock();
    }
  }

  private static final class InstanceHolder {
    // The single instance of the class
    private static final ThreadSafeStringArray instance = new ThreadSafeStringArray();
  }

  // Additional methods...
}
