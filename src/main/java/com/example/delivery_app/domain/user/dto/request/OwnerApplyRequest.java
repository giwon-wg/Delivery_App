package com.example.delivery_app.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OwnerApplyRequest {

	@NotBlank(message = "사업자등록번호는 필수입니다.")
	@JsonProperty("registrationNumber")
	private String registrationNumber;

	@NotBlank(message = "이름은 필수입니다.")
	@JsonProperty("ownerName")
	private String ownerName;
}
