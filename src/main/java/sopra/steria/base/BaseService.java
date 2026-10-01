package sopra.steria.base;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import sopra.steria.base.dto.BaseResponse;
import sopra.steria.base.dto.CreateBaseRequest;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BaseService {

    private final BaseRepository baseRepository;

    public BaseService(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    public List<BaseResponse> findAll() {
        return baseRepository.listAll().stream().map(
                        base -> new BaseResponse(
                                base.getId(),
                                base.getName(),
                                base.getLocation()))
                .toList();
    }

    @Transactional
    public BaseResponse create(CreateBaseRequest request) {
        Base base = new Base();
        base.setName(request.name());
        base.setLocation(request.location());

        baseRepository.persist(base);

        return toResponse(base);
    }

    public BaseResponse findById(UUID id) {
        Base base = baseRepository.findById(id);

        if (base == null) {
            throw new NotFoundException();
        }

        return toResponse(base);
    }

    private BaseResponse toResponse(Base base) {
        return new BaseResponse(
                base.getId(),
                base.getName(),
                base.getLocation()
        );
    }
}
