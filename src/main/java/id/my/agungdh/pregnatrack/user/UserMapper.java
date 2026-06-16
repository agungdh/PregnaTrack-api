package id.my.agungdh.pregnatrack.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Builder;

@Mapper(builder = @Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);
}