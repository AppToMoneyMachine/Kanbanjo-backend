package com.bliutvikler.bliutvikler.swimlane.controller;
import com.bliutvikler.bliutvikler.swimlane.model.Swimlane;
import com.bliutvikler.bliutvikler.swimlane.service.SwimlaneService;
import com.bliutvikler.bliutvikler.user.model.User;
import com.bliutvikler.bliutvikler.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/swimlane")
public class SwimlaneController {

    private static final Logger logger = LoggerFactory.getLogger(SwimlaneController.class);


    @Autowired
    private SwimlaneService swimlaneService;

    @Autowired
    private UserService userService;

    @PostMapping("/create/{boardId}")
    public ResponseEntity<Swimlane> createSwimlane(@PathVariable Long boardId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            Swimlane savedSwimlane = swimlaneService.createNewSwimlane(boardId, currentUser);
            logger.info("Swimlane created successfully with id -  {}", savedSwimlane.getId());
            return ResponseEntity.ok(savedSwimlane);
        } catch (IllegalStateException e) {
            logger.info("Limit for max number of swimlanes -  {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        catch (Exception e) {
            logger.info("Error with creating default swimlane {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete/{swimlaneId}/{boardId}")
    public ResponseEntity<Swimlane> deleteSwimlane(@PathVariable Long swimlaneId, @PathVariable Long boardId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            swimlaneService.deleteExistingSwimlane(swimlaneId, boardId, currentUser);
            logger.info("Swimlane deleted successfully with ID -  {}", swimlaneId);
            return ResponseEntity.ok().build(); // ingen body returneres pga delete
        } catch (IllegalStateException e) {
            logger.info("Minimum number of swimlanes reached -  {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        catch (Exception e) {
            logger.info("Error with deleting swimlane -  {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("update/{swimlaneId}")
    public ResponseEntity<Swimlane> updateSwimlaneName(@PathVariable Long swimlaneId, @RequestBody Swimlane newSwimlaneName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username); // get userobject from the database

            Swimlane swimlaneToUpdate = swimlaneService.updateSwimlaneName(swimlaneId, newSwimlaneName, currentUser);
            logger.info("Updated swimlane name successfully");
            return ResponseEntity.ok(swimlaneToUpdate);
        } catch (Exception e) {
            logger.info("Error with updateing swimlane name -  {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
