package com.example.GANerate.controller.notifyCationController;

import com.example.GANerate.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping(value = "/v1/subscribe", produces = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@RequestParam String token) {
        log.info("sub");
        return notificationService.subscribe(token);
    }

    @PostMapping("/v1/send-data/{id}")
    public void sendData(@PathVariable Long id) {
        notificationService.notify(id, "data");
    }
}
