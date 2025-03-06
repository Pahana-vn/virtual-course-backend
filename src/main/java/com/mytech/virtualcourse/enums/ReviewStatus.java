package com.mytech.virtualcourse.enums;

public enum ReviewStatus {
    PENDING,    // Review is waiting for approval
    PUBLISHED,  // Review is approved and visible
    HIDDEN,     // Review is temporarily hidden
    FLAGGED,    // Review has been flagged for review
    REJECTED    // Review has been rejected
}