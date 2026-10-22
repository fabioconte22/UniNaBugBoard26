package com.bugboard26.shared.user;

import com.bugboard26.shared.exception.UserNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserDirectory {
    
    Map<String, UserSummary> findByEmails(Collection<String> emails); 

    default void ensureUserExists(String email) {
        if (!findByEmails(List.of(email)).containsKey(email)) {
            throw new UserNotFoundException(email); 
        }
    }
}
