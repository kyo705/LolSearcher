package com.lolsearcher.user.identification;

import com.lolsearcher.user.ResponseSuccessDto;
import com.lolsearcher.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.lolsearcher.user.identification.IdentificationConstant.IDENTIFICATION_NUMBER_SIZE;
import static com.lolsearcher.user.identification.IdentificationConstant.IDENTIFICATION_URI;

@RequiredArgsConstructor
@RequestMapping(IDENTIFICATION_URI)
@RestController
public class IdentificationController {

    private final IdentificationService identificationService;

    @PostMapping
    public ResponseSuccessDto create(@PathVariable Long userId, @RequestBody IdentificationRequest request) {

        identificationService.create(userId, request);

        return new ResponseSuccessDto(true, "인증 번호 전송 성공");
    }

    @GetMapping
    public UserDto identify(
            @PathVariable Long id,
            @RequestParam @NotBlank @Pattern(regexp = "\\d")
            @Size(min = IDENTIFICATION_NUMBER_SIZE, max = IDENTIFICATION_NUMBER_SIZE) String requestNum
    ) {

        return identificationService.identify(id, requestNum);
    }
}
