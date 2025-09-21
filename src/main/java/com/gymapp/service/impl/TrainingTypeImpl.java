package com.gymapp.service.impl;

import com.gymapp.dto.request.trainingType.TrainingTypeRegistrationRequest;
import com.gymapp.dto.response.trainingtype.TrainingTypeResponse;
import com.gymapp.exception.ResourceAlreadyExistsException;
import com.gymapp.exception.ResourceNotFoundException;
import com.gymapp.model.TrainingType;
import com.gymapp.repository.TrainingTypeRepository;
import com.gymapp.service.AuthenticationService;
import com.gymapp.service.TrainingTypeService;
import com.gymapp.utils.Mappers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeImpl.class);
    private final Mappers mappers;

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
                .map(mappers::getTrainingTypeResponse)
                .toList();
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

        return mappers.getTrainingTypeResponse(savedTrainingType);
    }
}
