package cn.jxufe.valuexu.softwarestoreserver.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInResDTO {
    @JsonProperty("token")
    private String token;
    @JsonProperty("userInfo")
    private UserInfResDTO userInfResDTO;
}
