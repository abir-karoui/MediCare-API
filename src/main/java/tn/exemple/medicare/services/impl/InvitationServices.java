package tn.exemple.medicare.services.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.auth.User;
import tn.exemple.medicare.entities.invitation.Invitation;
import tn.exemple.medicare.entities.invitation.InvitationStatus;
import tn.exemple.medicare.entities.notification.Notification;
import tn.exemple.medicare.entities.notification.NotificationRequest;
import tn.exemple.medicare.enums.NotificationType;
import tn.exemple.medicare.enums.TypeRole;
import tn.exemple.medicare.exceptions.BusinessErrorCode;
import tn.exemple.medicare.exceptions.BusinessException;
import tn.exemple.medicare.repositories.IUserRepository;
import tn.exemple.medicare.repositories.InvitationRepository;
import tn.exemple.medicare.repositories.NotificationRepository;
import tn.exemple.medicare.services.IInvitation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvitationServices implements IInvitation {

    private final InvitationRepository invitationRepository;
    private final IUserRepository userRepository;
    private final AuthService authService;
    private final FCMService fcmService;
    private  final NotificationRepository notificationRepository;

    /*@Override
    public Invitation sendInvitation(Long receiverId) {
        Long senderId = authService.getAuthenticatedUserId();
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        if (sender.getRole() == receiver.getRole()) {
            throw new IllegalArgumentException("Sender and receiver cannot have the same role");
        }

        if ((sender.getRole() == TypeRole.PATIENT && receiver.getRole() != TypeRole.DOCTOR) ||
                (sender.getRole() == TypeRole.DOCTOR && receiver.getRole() != TypeRole.PATIENT)) {
            throw new IllegalArgumentException("Invalid invitation: roles not allowed");
        }

        Optional<Invitation> existingInvitation = invitationRepository.findPendingInvitation(senderId, receiverId);
        if (existingInvitation.isPresent()) {
            throw new BusinessException(BusinessErrorCode.INVITATION_ALREADY_SENT);
        }

        Invitation invitation = Invitation.builder()
                .sender(sender)
                .receiver(receiver)
                .senderType(sender.getRole())
                .receiverType(receiver.getRole())
                .status(InvitationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return invitationRepository.save(invitation);
    }*/

    @Override
    public Invitation sendInvitation(Long receiverId) {
        Long senderId = authService.getAuthenticatedUserId();
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        if (sender.getRole() == receiver.getRole()) {
            throw new IllegalArgumentException("Sender and receiver cannot have the same role");
        }

        if ((sender.getRole() == TypeRole.PATIENT && receiver.getRole() != TypeRole.DOCTOR) ||
                (sender.getRole() == TypeRole.DOCTOR && receiver.getRole() != TypeRole.PATIENT)) {
            throw new IllegalArgumentException("Invalid invitation: roles not allowed");
        }

        Optional<Invitation> existingInvitation = invitationRepository.findPendingInvitation(senderId, receiverId);
        if (existingInvitation.isPresent()) {
            throw new BusinessException(BusinessErrorCode.INVITATION_ALREADY_SENT);
        }

        Invitation invitation = Invitation.builder()
                .sender(sender)
                .receiver(receiver)
                .senderType(sender.getRole())
                .receiverType(receiver.getRole())
                .status(InvitationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Invitation savedInvitation = invitationRepository.save(invitation);

        if (receiver.getFcmToken() != null && !receiver.getFcmToken().isBlank()) {
            NotificationRequest notif = new NotificationRequest();
            notif.setTitle("New invitation");
            notif.setBody(sender.getFirstname()+ sender.getLastname() + " sent you an invitation" );
            notif.setToken(receiver.getFcmToken());

            try {
                fcmService.sendMessageToToken(notif);
                Notification entity = Notification.builder()
                        .title(notif.getTitle())
                        .body(notif.getBody())
                        .sentAt(LocalDateTime.now())
                        .type(NotificationType.REQUEST)
                        .invitation(invitation)
                        .user(receiver)
                        .build();

                notificationRepository.save(entity);
            } catch (Exception e) {
                System.out.println("Error sending notification : " + e.getMessage());
            }
        }

        return savedInvitation;
    }


    /*@Override
    public Invitation acceptInvitation(Long invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        checkIfReceiver(invitation);

        invitation.setStatus(InvitationStatus.ACCEPTED);
        return invitationRepository.save(invitation);
    }*/
    @Override
    public Invitation acceptInvitation(Long invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        checkIfReceiver(invitation);

        invitation.setStatus(InvitationStatus.ACCEPTED);
        Invitation updatedInvitationStatut = invitationRepository.save(invitation);

        User sender = invitation.getSender();
        User receiver = invitation.getReceiver();

        if (sender.getFcmToken() != null && !sender.getFcmToken().isBlank()) {
            NotificationRequest notif = new NotificationRequest();
            notif.setTitle("Invitation accepted");
            notif.setBody(receiver.getFirstname() + receiver.getLastname() +"  has accepted your invitation");
            notif.setToken(sender.getFcmToken());

            try {
                fcmService.sendMessageToToken(notif);
                Notification entity = Notification.builder()
                        .title(notif.getTitle())
                        .body(notif.getBody())
                        .sentAt(LocalDateTime.now())
                        .type(NotificationType.RESPONSE)
                        .invitation(invitation)
                        .user(sender)
                        .build();

                notificationRepository.save(entity);
            } catch (Exception e) {
                System.out.println("Erreur lors de l'envoi de la notification FCM : " + e.getMessage());
            }
        }

        return updatedInvitationStatut;
    }

    @Override
    @Transactional
    public void rejectInvitation(Long invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        notificationRepository.nullifyInvitationReferences(invitationId);
        invitationRepository.delete(invitation);
    }
    private Invitation getInvitationById(Long id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found with id: " + id));
    }
    private void checkIfReceiver(Invitation invitation) {
        Long currentUserId = authService.getAuthenticatedUserId();
        if (invitation.getReceiver().getId() != currentUserId) {
            throw new AccessDeniedException("You are not the receiver of this invitation");
        }
    }
    @Override
    public Optional<Invitation> getInvitationStatus(Long otherUserId) {
        Long currentUserId = authService.getAuthenticatedUserId();
        return invitationRepository.findLatestBetweenUsers(currentUserId, otherUserId);
    }

}
