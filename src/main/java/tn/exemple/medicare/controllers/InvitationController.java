package tn.exemple.medicare.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.entities.dto.UserDto;
import tn.exemple.medicare.entities.invitation.Invitation;
import tn.exemple.medicare.services.IInvitation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final IInvitation invitationService;

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<Invitation> sendInvitation(@PathVariable Long receiverId) {
        Invitation invitation = invitationService.sendInvitation(receiverId);
        return ResponseEntity.ok(invitation);
    }
    @PostMapping("/{id}/accept")
    public ResponseEntity<Invitation> acceptInvitation(@PathVariable Long id) {
        return ResponseEntity.ok(invitationService.acceptInvitation(id));
    }

    @DeleteMapping("/{id}/reject")
    public ResponseEntity<Void> rejectInvitation(@PathVariable Long id) {
        invitationService.rejectInvitation(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
    @GetMapping("/status/{otherUserId}")
    public ResponseEntity<?> getInvitationStatus(@PathVariable Long otherUserId) {
        Optional<Invitation> invitationOpt = invitationService.getInvitationStatus(otherUserId);

        if (invitationOpt.isEmpty()) {
            return ResponseEntity.ok().body(Map.of("status", "NONE"));
        }

        Invitation invitation = invitationOpt.get();

        return ResponseEntity.ok().body(Map.of(
                "id", invitation.getId(),
                "senderId", invitation.getSender().getId(),
                "receiverId", invitation.getReceiver().getId(),
                "status", invitation.getStatus().name()
        ));
    }
    @GetMapping("/connected-users")
    public ResponseEntity<List<UserDto>> getConnectedUsers() {
        List<UserDto> connectedUsers = invitationService.getConnectedUsers();
        return ResponseEntity.ok(connectedUsers);
    }
    @GetMapping("/is-connected/{userId}")
    public ResponseEntity<Boolean> areUsersConnected(@PathVariable Long userId) {
        boolean connected = invitationService.areUsersConnected(userId);
        return ResponseEntity.ok(connected);
    }
}
