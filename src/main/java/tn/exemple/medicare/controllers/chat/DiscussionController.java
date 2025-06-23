package tn.exemple.medicare.controllers.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.exemple.medicare.controllers.authcontrollers.AuthenticationResponse;
import tn.exemple.medicare.entities.chat.ChatMessageDto;
import tn.exemple.medicare.entities.chat.Discussion;
import tn.exemple.medicare.entities.chat.DiscussionResponseDTO;
import tn.exemple.medicare.repositories.ChatMessageRepository;
import tn.exemple.medicare.repositories.IDiscussionReposiroty;
import tn.exemple.medicare.services.IChatServices;

import java.util.List;


@RestController
@RequestMapping("/Discussion")
@RequiredArgsConstructor
public class DiscussionController {


    private  final IChatServices chatServices;
    public record UnreadDiscussions(List<Long> ids, int number) {}

    @GetMapping("/{discussionId}/messages")
    public Page<ChatMessageDto> getMessagesByDiscussion(
            @PathVariable Long discussionId,
            Pageable pageable
    ) {
        return chatServices.getMessagesByDiscussion(discussionId, pageable);
    }

    @GetMapping("/messages")
    public Page<DiscussionResponseDTO> getMyDiscussion(

            Pageable pageable
    ) {
        return chatServices.getUserDiscussions(pageable);
    }

    //@GetMapping("/{otherUserId}")
   /* public Page<ChatMessageDto> getMessagesWithUser(
            @PathVariable Long otherUserId,
            Pageable pageable
    ) {
        return chatServices.getMessagesWithUser(otherUserId, pageable);
    }
*/
    @GetMapping("/{otherUserId}")
    public Page<ChatMessageDto> getMessagesWithUser(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return chatServices.getMessagesWithUser(otherUserId, pageable);
    }

    @DeleteMapping("/delete/{discussionId}")
    public ResponseEntity<Void> deleteDiscussionForCurrentUser(@PathVariable Long discussionId) {
        chatServices.deleteDiscussionForUser(discussionId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        chatServices.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/messages/unread")
    public ResponseEntity<UnreadDiscussions> getUnreadDiscussionCount() {
        List<Long> ids = chatServices.getUnreadDiscussionIds();
        return ResponseEntity.ok(new UnreadDiscussions(ids, ids.size()));
    }




}
