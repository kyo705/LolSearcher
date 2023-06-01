package com.lolsearcher.search.summoner;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.lolsearcher.search.summoner.SummonerConstant.*;

@Validated
@RequiredArgsConstructor
@RestController
public class SummonerController {

	private final SummonerService summonerService;
	
	@GetMapping(FIND_BY_NAME_URI)
	public SummonerDto findByName(@PathVariable @NotBlank @Size(max = 50) String name) {

		String filteredName = filtering(name);

		return summonerService.findByName(filteredName);
	}

	private String filtering(String unfilteredName) {

		String filteredName = unfilteredName.replaceAll(SUMMONER_NAME_REGEX, "");

		if(filteredName.isBlank()){
			throw new IllegalArgumentException(
					String.format("name parameter's length must be between %s and %s except special character",
							SUMMONER_NAME_MIN_LENGTH, SUMMONER_NAME_MAX_LENGTH)
			);
		}
		return  filteredName;
	}
}
