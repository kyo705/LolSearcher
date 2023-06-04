package com.lolsearcher.user.identification;

import com.lolsearcher.user.ResponseSuccessDto;
import com.lolsearcher.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.lolsearcher.user.identification.IdentificationConstant.IDENTIFICATION_NUMBER_SIZE;
import static com.lolsearcher.user.identification.IdentificationConstant.IDENTIFICATION_URI;

@Validated
@RequiredArgsConstructor
@RestController
public class IdentificationController {

    private final IdentificationService identificationService;

    @PostMapping(IDENTIFICATION_URI)
    public ResponseSuccessDto create(@PathVariable Long userId, @RequestBody @Valid IdentificationRequest request) {

        request.getDevice().validate(request.getDeviceValue());
        identificationService.create(userId, request);

        return new ResponseSuccessDto(true, "인증 번호 전송 성공");
    }

    @GetMapping(IDENTIFICATION_URI)
    public UserDto identify(
            @PathVariable Long userId,
            @RequestParam @NotBlank @Pattern(regexp = "^\\d*$") @Size(min = IDENTIFICATION_NUMBER_SIZE, max = IDENTIFICATION_NUMBER_SIZE) String code
    ) {

        return identificationService.identify(userId, code);
    }
}
