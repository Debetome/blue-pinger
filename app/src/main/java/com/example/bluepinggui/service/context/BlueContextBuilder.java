package com.example.bluepinggui.service.context;

import com.example.bluepinggui.service.strategy.BlueService;

/*
** Usage: **

BlueContext myBlueContext = new BlueContextBuilder()
        .service(myService)
        .contextName("My context")
        .build();

** Extensive usage: **

BlueContext myBlueContext = new BlueContextBuilder()
        .service(myService)
        .contextName("Context name")
        .threadCount(30)
        .minAttempts(4)
        .maxAttempts(6)
        .build();

*/

public class BlueContextBuilder {
    // Private blue context fields
    private BlueService blueService;
    private String contextName;

    // Default to some relatively convenient values
    private int threadCount = 1;
    private int minAttempts = 5;
    private int maxAttempts = 5;

    // Fluent setters (return the builder for method chaining)
    public BlueContextBuilder service(BlueService blueService) {
        this.blueService = blueService;
        return this;
    }

    public BlueContextBuilder contextName(String contextName) {
        this.contextName = contextName;
        return this;
    }

    public BlueContextBuilder threadCount(int threadCount) {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be positive");
        }
        this.threadCount = threadCount;
        return this;
    }

    public BlueContextBuilder minAttempts(int minAttempts) {
        if (minAttempts < 0) {
            throw new IllegalArgumentException("Min attempts cannot be negative");
        }
        this.minAttempts = minAttempts;
        return this;
    }

    public BlueContextBuilder maxAttempts(int maxAttempts) {
        if (maxAttempts < minAttempts) {
            throw new IllegalArgumentException("Max attempts must be greater than or equal to min attempts");
        }
        this.maxAttempts = maxAttempts;
        return this;
    }

    // Build method to create the BlueContext object
    public BlueContext build() {
        if (blueService == null || contextName == null) {
            throw new IllegalStateException("Service and context name are required");
        }
        return new BlueContext(this);
    }

    // Getters for the fields (optional, but useful for testing or debugging)
    public BlueService getService() {
        return blueService;
    }

    public String getContextName() {
        return contextName;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getMinAttempts() {
        return minAttempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }
}