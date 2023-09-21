package com.example.GANerate.controller.notifyCationController;

import com.example.GANerate.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping(value = "/v1/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        log.info("sub");
        return notificationService.subscribe();
    }

    @PostMapping("/v1/send-data/{id}")
    public void sendData(@PathVariable Long id) {
        notificationService.notify(id, "data");
    }
}
