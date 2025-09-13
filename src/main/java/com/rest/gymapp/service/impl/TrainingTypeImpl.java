package com.rest.gymapp.service.impl;

import com.rest.gymapp.dto.request.trainingType.TrainingTypeRegistrationRequest;
import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import com.rest.gymapp.exception.ResourceAlreadyExistsException;
import com.rest.gymapp.exception.ResourceNotFoundException;
import com.rest.gymapp.model.TrainingType;
import com.rest.gymapp.repository.TrainingTypeRepository;
import com.rest.gymapp.service.AuthenticationService;
import com.rest.gymapp.service.TraineeService;
import com.rest.gymapp.service.TrainingTypeService;
import com.rest.gymapp.utils.Mappers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingTypeImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private final Mappers mapper;

    @Override
    public List<TrainingTypeResponse> getAllTrainingTypes(String username, String password, String transactionId) {

        logger.info("[{}] Fetching all training types...", transactionId);

        authenticationService.authenticateUser(username, password);

        List<TrainingType> types = trainingTypeRepository.findAll();

        if (types.isEmpty()) {
            logger.info("[{}] Failed to fetch training types", transactionId);
            throw new ResourceNotFoundException("Failed to fetch training types");
        }

        logger.info("[{}] training types list successfully retrieved", transactionId);

        return types.stream()
                .map(mapper::getTrainingTypeResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public TrainingTypeResponse addTrainingType(TrainingTypeRegistrationRequest req, String username, String password, String transactionId) {

        authenticationService.authenticateTrainer(username, password);

        logger.info("[{}] {} is created training type : {}", transactionId, username, req.trainingTypeName());

        if (trainingTypeRepository.findByTrainingTypeName(req.trainingTypeName()).isPresent()) {
            logger.info("[{}] failed to create training type as it already exists", transactionId);
            throw new ResourceAlreadyExistsException("Training Type already exists");
        }

        TrainingType savedTrainingType = trainingTypeRepository.save(
                new TrainingType(req.trainingTypeName())
        );

        logger.info("[{}] successfully created training type: {}", transactionId, req.trainingTypeName());

        return mapper.getTrainingTypeResponse(savedTrainingType);
    }
}
