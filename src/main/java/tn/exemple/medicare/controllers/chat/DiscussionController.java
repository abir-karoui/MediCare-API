package tn.exemple.medicare.controllers.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.exemple.medicare.entities.chat.ChatMessageDto;
import tn.exemple.medicare.entities.chat.Discussion;
import tn.exemple.medicare.entities.chat.DiscussionResponseDTO;
import tn.exemple.medicare.repositories.ChatMessageRepository;
import tn.exemple.medicare.repositories.IDiscussionReposiroty;
import tn.exemple.medicare.services.IChatServices;


@RestController
@RequestMapping("/Discussion")
@RequiredArgsConstructor
public class DiscussionController {

    private final ChatMessageRepository chatMessageRepository;

    private final IDiscussionReposiroty discussionReposiroty;
    private  final IChatServices chatServices;
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

    @GetMapping("/{otherUserId}")
    public Page<ChatMessageDto> getMessagesWithUser(
            @PathVariable Long otherUserId,
            Pageable pageable
    ) {
        return chatServices.getMessagesWithUser(otherUserId, pageable);
    }
}
