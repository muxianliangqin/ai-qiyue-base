package com.qiyue.infrastructure.utils;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RetryUtil {

    /**
     * 超时限制
     */
    public static final long TIMEOUT = 3000L;
    public static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    public static TimeoutRetryPolicy createTimeoutRetry(long timeout, TimeUnit timeUnit) {
        if (Objects.nonNull(TIMEOUT_RETRY_POLICY) && timeout == TIMEOUT && timeUnit.compareTo(TIME_UNIT) == 0) {
            return TIMEOUT_RETRY_POLICY;
        }
        TimeoutRetryPolicy timeoutRetryPolicy = new TimeoutRetryPolicy();
        timeoutRetryPolicy.setTimeout(timeUnit.toNanos(timeout));
        return timeoutRetryPolicy;
    }

    public static TimeoutRetryPolicy createTimeoutRetry() {
        return createTimeoutRetry(TIMEOUT, TIME_UNIT);
    }

    /**
     * 最大重试次数
     */
    public static final int MAX_ATTEMPTS = 100;

    public static MaxAttemptsRetryPolicy createMaxAttemptsRetry(int maxAttempts) {
        if (Objects.nonNull(MAX_ATTEMPTS_RETRY_POLICY) && maxAttempts == MAX_ATTEMPTS) {
            return MAX_ATTEMPTS_RETRY_POLICY;
        }
        MaxAttemptsRetryPolicy maxAttemptsRetryPolicy = new MaxAttemptsRetryPolicy();
        maxAttemptsRetryPolicy.setMaxAttempts(maxAttempts);
        return maxAttemptsRetryPolicy;
    }

    public static MaxAttemptsRetryPolicy createMaxAttemptsRetry() {
        return createMaxAttemptsRetry(MAX_ATTEMPTS);
    }

    /**
     * 固定重试间隔
     */
    public static final long INTERVAL = 10L;

    public static FixedBackOffPolicy createFixedBackOff(long interval) {
        if (Objects.nonNull(FIXED_BACK_OFF_POLICY) && interval == INTERVAL) {
            return FIXED_BACK_OFF_POLICY;
        }
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(interval);
        return fixedBackOffPolicy;
    }

    public static FixedBackOffPolicy createFixedBackOff() {
        return createFixedBackOff(INTERVAL);
    }

    /**
     * 指数增长重试间隔
     */
    public static final double MULTIPLIER = 2;
    public static final long MAX_INTERVAL = INTERVAL * 16;

    public static ExponentialBackOffPolicy createExponentialBackOff(long interval, double multiplier, long maxInterval) {
        if (Objects.nonNull(EXPONENTIAL_BACK_OFF_POLICY) &&
                interval == INTERVAL
                && BigDecimal.valueOf(multiplier).compareTo(BigDecimal.valueOf(MULTIPLIER)) == 0
                && maxInterval == MAX_INTERVAL
        ) {
            return EXPONENTIAL_BACK_OFF_POLICY;
        }
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(interval);
        exponentialBackOffPolicy.setMultiplier(multiplier);
        exponentialBackOffPolicy.setMaxInterval(maxInterval);
        return exponentialBackOffPolicy;
    }

    public static ExponentialBackOffPolicy createExponentialBackOff() {
        return createExponentialBackOff(INTERVAL, MULTIPLIER, MAX_INTERVAL);
    }

    private static final FixedBackOffPolicy FIXED_BACK_OFF_POLICY;
    private static final ExponentialBackOffPolicy EXPONENTIAL_BACK_OFF_POLICY;
    private static final TimeoutRetryPolicy TIMEOUT_RETRY_POLICY;
    private static final MaxAttemptsRetryPolicy MAX_ATTEMPTS_RETRY_POLICY;
    private static final RetryTemplateBuilder RETRY_TEMPLATE_BUILDER = RetryTemplate.builder();

    private static final RetryTemplate FIX_RETRY;
    private static final RetryTemplate EXPONENTIAL_RETRY;

    static {
        TIMEOUT_RETRY_POLICY = createTimeoutRetry();
        MAX_ATTEMPTS_RETRY_POLICY = createMaxAttemptsRetry();
        CompositeRetryPolicy compositeRetryPolicy = new CompositeRetryPolicy();
        compositeRetryPolicy.setPolicies(new RetryPolicy[]{TIMEOUT_RETRY_POLICY, MAX_ATTEMPTS_RETRY_POLICY});
        FIXED_BACK_OFF_POLICY = createFixedBackOff();
        FIX_RETRY = RETRY_TEMPLATE_BUILDER.customPolicy(compositeRetryPolicy)
                .customBackoff(FIXED_BACK_OFF_POLICY)
                .retryOn(IllegalStateException.class).build();
        EXPONENTIAL_BACK_OFF_POLICY = createExponentialBackOff();
        EXPONENTIAL_RETRY = RETRY_TEMPLATE_BUILDER.customPolicy(compositeRetryPolicy)
                .customBackoff(EXPONENTIAL_BACK_OFF_POLICY)
                .build();
    }

    public static RetryTemplate defaultFixRetry() {
        return FIX_RETRY;
    }

    public static RetryTemplate defaultExponentialRetry() {
        return EXPONENTIAL_RETRY;
    }

    public static RetryTemplate createFixRetry(int maxAttempts, long timeout, TimeUnit timeUnit, long interval) {
        MaxAttemptsRetryPolicy maxAttemptsRetryPolicy = createMaxAttemptsRetry(maxAttempts);
        TimeoutRetryPolicy timeoutRetryPolicy = createTimeoutRetry(timeout, timeUnit);
        CompositeRetryPolicy compositeRetryPolicy = new CompositeRetryPolicy();
        compositeRetryPolicy.setPolicies(new RetryPolicy[]{timeoutRetryPolicy, maxAttemptsRetryPolicy});
        FixedBackOffPolicy fixedBackOffPolicy = createFixedBackOff(interval);
        return RETRY_TEMPLATE_BUILDER.customPolicy(compositeRetryPolicy)
                .customBackoff(fixedBackOffPolicy)
                .retryOn(IllegalStateException.class).build();
    }

    public static RetryTemplate createExponentialRetry(int maxAttempts, long timeout, TimeUnit timeUnit,
                                                       long interval, double multiplier, long maxInterval) {
        MaxAttemptsRetryPolicy maxAttemptsRetryPolicy = createMaxAttemptsRetry(maxAttempts);
        TimeoutRetryPolicy timeoutRetryPolicy = createTimeoutRetry(timeout, timeUnit);
        CompositeRetryPolicy compositeRetryPolicy = new CompositeRetryPolicy();
        compositeRetryPolicy.setPolicies(new RetryPolicy[]{timeoutRetryPolicy, maxAttemptsRetryPolicy});
        ExponentialBackOffPolicy exponentialBackOff = createExponentialBackOff(interval, multiplier, maxInterval);
        return RETRY_TEMPLATE_BUILDER.customPolicy(compositeRetryPolicy)
                .customBackoff(exponentialBackOff)
                .retryOn(IllegalStateException.class).build();
    }
}
