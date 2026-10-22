package com.bugboard26.modules.notification.controller;

import com.bugboard26.modules.notification.dto.NotificationResponse;
import com.bugboard26.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService; 

    @GetMapping 
    public ResponseEntity<List<NotificationResponse>> getUnread(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(notificationService.getUnread(email)); 
    }

    @GetMapping("/history")
    public ResponseEntity<List<NotificationResponse>> getHistory(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(notificationService.getHistory(email)); 
    }

    @PatchMapping("{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id, @AuthenticationPrincipal String email) {
        notificationService.markAsRead(id, email);
        return ResponseEntity.noContent().build();
    } 

}
