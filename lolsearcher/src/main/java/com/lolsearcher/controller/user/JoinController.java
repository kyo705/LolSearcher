package com.lolsearcher.controller.user;

import com.lolsearcher.model.request.user.join.RequestEmailCheckDto;
import com.lolsearcher.model.request.user.join.RequestUserJoinDto;
import com.lolsearcher.model.response.front.user.ResponseSuccessDto;
import com.lolsearcher.service.user.join.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class JoinController {

	private final JoinService userService;

	@PostMapping(path = "/join")
	public ResponseEntity<ResponseSuccessDto> processJoin(@RequestBody @Valid RequestUserJoinDto requestDto) {

		validateUserJoinRequest(requestDto);

		return userService.handleJoinProcedure(requestDto);
	}

	@PostMapping(path = "/join/check")
	public ResponseSuccessDto checkPossibleEmail(@RequestBody @Valid RequestEmailCheckDto requestDto) {

		return userService.checkPossibleEmail(requestDto);
	}

	private void validateUserJoinRequest(RequestUserJoinDto requestDto) {

		String username = requestDto.getUsername();

		for(char c : username.toCharArray()) {
			if((c>='a'&& c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')) {
				continue;
			}
			throw new IllegalArgumentException();
		}
	}
}
