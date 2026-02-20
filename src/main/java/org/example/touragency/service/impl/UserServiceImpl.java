package org.example.touragency.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.touragency.dto.event.SystemEvent;
import org.example.touragency.dto.request.RegisterRequest;
import org.example.touragency.dto.response.UserResponseDto;
import org.example.touragency.dto.response.UserUpdateDto;
import org.example.touragency.enums.EventStatus;
import org.example.touragency.enums.EventType;
import org.example.touragency.exception.BadRequestException;
import org.example.touragency.exception.ConflictException;
import org.example.touragency.exception.NotFoundException;
import org.example.touragency.enums.Role;
import org.example.touragency.model.entity.OutboxEvent;
import org.example.touragency.model.entity.Tour;
import org.example.touragency.model.entity.User;
import org.example.touragency.repository.*;
import org.example.touragency.service.abstractions.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final FavTourRepository favTourRepository;
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final RatingCounterRepository ratingCounterRepository;
    private final PasswordEncoder passwordEncoder;
    private final OutboxService outboxService;


    @Transactional
    @Override

    public User register(RegisterRequest request) {
        if (request == null) {
            throw new BadRequestException("UserAddDto cannot be null");
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new ConflictException("The phone number already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("The email already exists");
        }

        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .build();

        userRepository.save(newUser);

        outboxService.createAndSaveOutboxEvent(
                EventType.USER_REGISTERED,
                String.valueOf(newUser.getId()),
                newUser.getId(),
                newUser
        );

        return newUser;
    }

    @Override
    @Transactional
    public UserResponseDto addNewAgency(UUID agencyId) {
        User agency = userRepository.findById(agencyId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        agency.setRole(Role.AGENCY);
        userRepository.save(agency);

        UserResponseDto agencyDto = toResponseDto(agency);

        outboxService.createAndSaveOutboxEvent(
                EventType.AGENCY_CREATED,
                String.valueOf(agencyId),
                agencyId,
                agencyDto
        );

        return agencyDto;
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        if (userId == null) {
            throw new BadRequestException("UserId cannot be null");
        }

        userRepository.findById(userId).ifPresent(user -> {

            if (user.getRole() == Role.AGENCY) {

                UserResponseDto payload = toResponseDto(user);

                outboxService.createAndSaveOutboxEvent(
                        EventType.AGENCY_DELETED,
                        String.valueOf(user.getId()),
                        user.getId(),
                        payload
                );

                List<Tour> tours = tourRepository.findAllByAgencyId(user.getId());

                for (Tour tour : tours) {
                    bookingRepository.deleteByTourId(tour.getId());
                    favTourRepository.deleteByTourId(tour.getId());
                    ratingCounterRepository.deleteByTourId(tour.getId());
                    ratingRepository.deleteByTourId(tour.getId());
                }

                tourRepository.deleteByAgencyId(user.getId());
            }

            UserResponseDto payload = toResponseDto(user);

            outboxService.createAndSaveOutboxEvent(
                    EventType.USER_DELETED,
                    String.valueOf(user.getId()),
                    user.getId(),
                    payload
            );


            ratingRepository.deleteByUserId(userId);
            favTourRepository.deleteByUserId(userId);
            bookingRepository.deleteByUserId(userId);

            userRepository.deleteById(userId);
        });
    }


    @Override
    @Transactional
    public UserResponseDto updateUser(UUID userId, UserUpdateDto dto) {

        if (userId == null || dto == null) {
            throw new BadRequestException("Parameters cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.findByEmail(dto.getEmail())
                .filter(u -> !u.getId().equals(userId))
                .ifPresent(u -> {
                    throw new ConflictException("Email already in use");
                });

        userRepository.findByPhoneNumber(dto.getPhoneNumber())
                .filter(u -> !u.getId().equals(userId))
                .ifPresent(u -> {
                    throw new ConflictException("Phone number already in use");
                });

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        userRepository.save(user);

        UserResponseDto updatedUserDto = toResponseDto(user);

        if(user.getRole() == Role.AGENCY){
            outboxService.createAndSaveOutboxEvent(
                    EventType.AGENCY_UPDATED,
                    String.valueOf(user.getId()),
                    user.getId(),
                    updatedUserDto
            );
        }else {
            outboxService.createAndSaveOutboxEvent(
                    EventType.AGENCY_UPDATED,
                    String.valueOf(user.getId()),
                    user.getId(),
                    updatedUserDto
            );
        }

        return updatedUserDto;
    }


    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }


    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserResponseDto toResponseDto(User user) {

        Optional<User> optionalUser = userRepository.findById(user.getId());
        if(optionalUser.isEmpty()){
            throw new NotFoundException("User not found");
        }

        return UserResponseDto.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }

}