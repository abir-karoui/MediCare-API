package tn.exemple.medicare.services;

import tn.exemple.medicare.entities.invitation.Invitation;

import java.util.Optional;

public interface IInvitation {
    Invitation sendInvitation(Long receiverId);
    Invitation acceptInvitation(Long invitationId);
    Invitation rejectInvitation(Long invitationId);
    Optional<Invitation> getInvitationStatus(Long otherUserId);

}
