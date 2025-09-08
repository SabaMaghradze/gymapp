package com.rest.gymapp.service.impl;

import com.rest.gymapp.dto.response.trainingtype.TrainingTypeResponse;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingTypeImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private final Mappers mapper;

    @Override
    public List<TrainingTypeResponse> getAllTrainingTypes(String username, String password) {

        logger.info("Fetching all training types...");

        authenticationService.authenticateUser(username, password);

        List<TrainingType> types = trainingTypeRepository.findAll();

        if (types.isEmpty()) {
            throw new ResourceNotFoundException("Failed to fetch training types");
        }

        return types.stream()
                .map(mapper::getTrainingTypeResponse)
                .collect(Collectors.toUnmodifiableList());
    }
}
