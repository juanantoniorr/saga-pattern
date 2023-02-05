package com.app.estore.ProductService.rest;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/management")
public class EventsReplayController {
    @Autowired
    EventProcessingConfiguration eventProcessingConfiguration;

    @PostMapping("/eventProcessor/{procesorName}/reset")
    public ResponseEntity<?> replayEvents(@PathVariable String processorName) {
        Optional<TrackingEventProcessor> eventProcessor = eventProcessingConfiguration.eventProcessor(processorName, TrackingEventProcessor.class);
        if (eventProcessor.isPresent()) {
            TrackingEventProcessor trackingEventProcessor = eventProcessor.get();
            trackingEventProcessor.shutDown();
            trackingEventProcessor.resetTokens();
            trackingEventProcessor.start();
            return ResponseEntity.ok("Event processor " + trackingEventProcessor.getName() + " was successfully reset");

        } else {
            return ResponseEntity.badRequest().body("Event processor not found");
        }

    }
}
