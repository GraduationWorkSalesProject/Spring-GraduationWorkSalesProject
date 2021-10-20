package GraduationWorkSalesProject.graduation.com.dto.member;

import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
public class MemberLoginRequest {

    @ApiParam(value = "아이디", example = "example123", required = true)
    @Pattern(regexp = "^[a-z0-9+]{5,20}$", message = "아이디는 5~20자의 영소문자, 숫자만 사용 가능합니다.")
    @NotEmpty(message = "아이디를 입력해주세요.")
    private String userid;

    @ApiParam(value = "비밀번호(숫자, 문자, 특수문자를 포함한 8자리 이상, 15자리 이하)", example = "a1B2C!d#4ss", required = true)
    @Pattern(regexp = "^.*(?=^.{8,15}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$",
            message = "비밀번호는 숫자, 문자, 특수문자를 포함한 8자리 이상, 15자리 이하로 입력해주세요.")
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    private String password;
}
