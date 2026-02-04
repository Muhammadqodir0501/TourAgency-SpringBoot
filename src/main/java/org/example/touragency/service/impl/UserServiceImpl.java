package org.example.touragency.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.touragency.dto.request.UserAddDto;
import org.example.touragency.dto.response.UserResponseDto;
import org.example.touragency.dto.response.UserUpdateDto;
import org.example.touragency.exception.BadRequestException;
import org.example.touragency.exception.ConflictException;
import org.example.touragency.exception.NotFoundException;
import org.example.touragency.model.Role;
import org.example.touragency.model.entity.Tour;
import org.example.touragency.model.entity.User;
import org.example.touragency.repository.*;
import org.example.touragency.service.abstractions.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final FavTourRepository favTourRepository;
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;

    @Override
    public UserResponseDto addNewUser(UserAddDto dto) {
        if (dto == null) {
            throw new BadRequestException("UserAddDto cannot be null");
        }
        if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new ConflictException("The phone number already exists");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("The email already exists");
        }

        User newUser = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .phoneNumber(dto.getPhoneNumber())
                .role(dto.getRole())
                .build();

        userRepository.save(newUser);
        return toResponseDto(newUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        if (userId == null) {
            throw new BadRequestException("UserId cannot be null");
        }

        userRepository.findById(userId).ifPresent(user -> {

            if (user.getRole() == Role.AGENCY) {

                List<Tour> tours = tourRepository.findByAgencyId(user.getId());

                for (Tour tour : tours) {
                    bookingRepository.deleteAllIfTourDeleted(tour.getId());
                    favTourRepository.deleteAllIfTourDeleted(tour.getId());
                    ratingRepository.deleteAllRatingsIfTourDeleted(tour.getId());
                    ratingRepository.deleteAllCountersIfTourDeleted(tour.getId());
                }

                tourRepository.deleteAllByAgencyId(user.getId());
            }
            ratingRepository.deleteAllIfUserDeleted(userId);
            favTourRepository.deleteAllIfUserDeleted(userId);
            bookingRepository.deleteAllIfUserDeleted(userId);

            userRepository.deleteById(userId);
        });
    }


    @Override
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

        userRepository.update(user);
        return toResponseDto(user);
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