package pungmul.pungmul.service.member.membermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.dto.member.UpdateInstrumentRequestDTO;
import pungmul.pungmul.dto.member.UpdateInstrumentResponseDTO;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.domain.member.user.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstrumentService {
    private final InstrumentStatusRepository instrumentStatusRepository;
    private final UserRepository userRepository;

    public List<Long> createInstrument(Long accountId, List<InstrumentStatus> instrumentStatusList) {
        Long userId = userRepository.getUserIdByAccountId(accountId);
        return instrumentStatusList.stream()
                .map(status -> {
                    InstrumentStatus instrumentStatus = InstrumentStatus.builder()
                            .userId(userId)
                            .instrument(status.getInstrument())
                            .instrumentAbility(status.getInstrumentAbility())
                            .major(status.isMajor())
                            .build();
                    instrumentStatusRepository.saveInstrument(instrumentStatus);
                    return instrumentStatus.getId();
                }).collect(Collectors.toList());
    }

    public UpdateInstrumentResponseDTO updateInstrumentStatus(String email, UpdateInstrumentRequestDTO updateInstrumentRequestDTO) {
        Long userId = userRepository.getUserByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        InstrumentStatus instrumentStatus = InstrumentStatus.builder()
                .userId(userId)
                .instrument(updateInstrumentRequestDTO.getInstrument())
                .instrumentAbility(updateInstrumentRequestDTO.getInstrumentAbility())
                .major(updateInstrumentRequestDTO.getMajor())
                .build();

        instrumentStatusRepository.setMajorFalseForOtherInstruments(instrumentStatus);
        instrumentStatusRepository.updateInstrumentAbilityAndMajor(instrumentStatus);

        List<InstrumentStatus> updatedStatuses = instrumentStatusRepository.getAllInstrumentStatusByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("No instruments found"));

        return UpdateInstrumentResponseDTO.builder().instruments(updatedStatuses).build();
    }
}
