package roomescape.reservation.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.response.ThemeResponse;
import roomescape.reservation.dto.request.ThemeSaveRequest;
import roomescape.reservation.application.ThemeService;

import java.util.List;

@RestController
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody ThemeSaveRequest request) {
        Theme theme = request.toModel();
        return ResponseEntity.status(HttpStatus.CREATED).body(themeService.create(theme));
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        return ResponseEntity.ok(themeService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> findAllPopular() {
        return ResponseEntity.ok(themeService.findAllPopular());
    }
}