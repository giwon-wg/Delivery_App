package com.example.delivery_app.domain.menu.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MenuOptionRequestDto {

	@Schema(description = "옵션 이름", example = "계란")
	@NotBlank(message = "옵션명을 입력해주세요")
	@Size(max = 20, message = "옵션명은 20자 이내로 입력해주세요")
	@JsonProperty("optionName")
	private final String optionName;

	@Schema(description = "옵션 가격", example = "200")
	@NotNull(message = "가격을 입력해주세요")
	@JsonProperty("price")
	private final Integer price;

	@Schema(description = "옵션 설명", example = "날계란")
	@NotBlank(message = "옵션 설명을 입력해주세요")
	@JsonProperty("content")
	private final String content;

}
