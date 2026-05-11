package finalproject_cinemabooking.controller;

import finalproject_cinemabooking.model.dto.LoginRequest;
import finalproject_cinemabooking.model.dto.RegisterRequest;
import finalproject_cinemabooking.model.dto.MonthlyRevenueView;
import finalproject_cinemabooking.model.dto.TopMovieRevenueView;
import finalproject_cinemabooking.model.entity.Movie;
import finalproject_cinemabooking.model.entity.Room;
import finalproject_cinemabooking.model.entity.Showtime;
import finalproject_cinemabooking.model.entity.Ticket;
import finalproject_cinemabooking.model.entity.User;
import finalproject_cinemabooking.model.entity.UserProfile;
import finalproject_cinemabooking.repository.BookingRepository;
import finalproject_cinemabooking.repository.GenreRepository;
import finalproject_cinemabooking.repository.MovieRepository;
import finalproject_cinemabooking.repository.SeatRepository;
import finalproject_cinemabooking.repository.ShowtimeRepository;
import finalproject_cinemabooking.repository.TicketRepository;
import finalproject_cinemabooking.repository.UserProfileRepository;
import finalproject_cinemabooking.repository.UserRepository;
import finalproject_cinemabooking.service.AuthService;
import finalproject_cinemabooking.service.ShowtimeService;
import finalproject_cinemabooking.util.BCryptUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ViewController {
    private final AuthService authService;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final ShowtimeService showtimeService;
    private final BookingRepository bookingRepository;
    private final GenreRepository genreRepository;

    public ViewController(AuthService authService, UserProfileRepository userProfileRepository, UserRepository userRepository, MovieRepository movieRepository, ShowtimeRepository showtimeRepository, SeatRepository seatRepository, TicketRepository ticketRepository, ShowtimeService showtimeService, BookingRepository bookingRepository, GenreRepository genreRepository) {
        this.authService = authService;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.showtimeService = showtimeService;
        this.bookingRepository = bookingRepository;
        this.genreRepository = genreRepository;
    }

    @GetMapping({"/", "/login"})
    public String loginPage(@RequestParam(required = false) String error, Model model, HttpSession session) {
        if (session.getAttribute("USER_ID") != null) {
            return "redirect:/home";
        }
        if ("access_denied".equals(error)) {
            model.addAttribute("authError", "Bạn không có quyền truy cập trang này. Vui lòng đăng nhập lại.");
        }
        if (!model.containsAttribute("fieldErrors")) {
            model.addAttribute("fieldErrors", new LinkedHashMap<String, String>());
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/home")
    public String homePage(@RequestParam(required = false) String genre,
                           @RequestParam(required = false) String keyword,
                           Model model, HttpSession session) {
        List<Movie> movies;
        if (genre != null && !genre.isEmpty() && keyword != null && !keyword.isEmpty()) {
            movies = movieRepository.findByGenreAndTitleContainingIgnoreCase(genre, keyword);
        } else if (genre != null && !genre.isEmpty()) {
            movies = movieRepository.findByGenre(genre);
        } else if (keyword != null && !keyword.isEmpty()) {
            movies = movieRepository.findByTitleContainingIgnoreCase(keyword);
        } else {
            movies = movieRepository.findAll();
        }
        model.addAttribute("movies", movies);
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("keyword", keyword);
        Long userId = (Long) session.getAttribute("USER_ID");
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        model.addAttribute("currentUserName", profile != null ? profile.getFullName() : "bạn");
        return "home";
    }

    @GetMapping("/showtimes")
    public String showtimesPage(@RequestParam Long movieId, Model model, HttpSession session) {
        showtimeService.updateShowtimeStatus();
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        List<Showtime> showtimes = showtimeRepository.findByMovieIdAndStatusNot(movieId, Showtime.ShowtimeStatus.DELETED);
        
        // Group showtimes by room
        Map<Room, List<Showtime>> showtimesByRoom = showtimes.stream()
                .collect(Collectors.groupingBy(Showtime::getRoom));
                
        model.addAttribute("movie", movie);
        model.addAttribute("showtimesByRoom", showtimesByRoom);

        Long userId = (Long) session.getAttribute("USER_ID");
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        model.addAttribute("currentUserName", profile != null ? profile.getFullName() : "bạn");
        return "showtimes";
    }

    @GetMapping("/seats")
    public String seatsPage(@RequestParam Long showtimeId, Model model, HttpSession session) {
        Showtime showtime = showtimeRepository.findByIdWithMovieAndRoom(showtimeId).orElseThrow();
        List<Ticket> bookedTickets = ticketRepository.findConfirmedTicketsByShowtime(showtimeId);
        Set<Long> bookedSeatIds = bookedTickets.stream().map(t -> t.getSeat().getId()).collect(Collectors.toSet());

        model.addAttribute("showtime", showtime);
        model.addAttribute("seats", seatRepository.findByRoomId(showtime.getRoom().getId()));
        model.addAttribute("bookedSeatIds", bookedSeatIds);
        Long userId = (Long) session.getAttribute("USER_ID");
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        model.addAttribute("currentUserName", profile != null ? profile.getFullName() : "bạn");
        return "seats";
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        model.addAttribute("currentUserName", profile != null ? profile.getFullName() : "bạn");
        return "checkout";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);

        Double totalRevenue30d = bookingRepository.sumRevenueSince(startDate);
        long ticketsSold30d = ticketRepository.countConfirmedTicketsSince(startDate);
        long activeMovies = showtimeRepository.countActiveMovies();

        List<MonthlyRevenueView> monthlyRevenue = bookingRepository.findMonthlyRevenue();
        List<TopMovieRevenueView> topMovies = bookingRepository.findTopMoviesByRevenue(PageRequest.of(0, 5));

        List<String> monthlyLabels = monthlyRevenue.stream()
                .map(MonthlyRevenueView::getMonth)
                .collect(Collectors.toList());
        List<Double> monthlyValues = monthlyRevenue.stream()
                .map(item -> item.getTotal() == null ? 0d : item.getTotal())
                .collect(Collectors.toList());

        List<String> topMovieLabels = topMovies.stream()
                .map(TopMovieRevenueView::getTitle)
                .collect(Collectors.toList());
        List<Double> topMovieValues = topMovies.stream()
                .map(item -> item.getTotal() == null ? 0d : item.getTotal())
                .collect(Collectors.toList());

        model.addAttribute("totalRevenue30d", totalRevenue30d == null ? 0d : totalRevenue30d);
        model.addAttribute("ticketsSold30d", ticketsSold30d);
        model.addAttribute("activeMovies", activeMovies);
        model.addAttribute("monthlyLabels", monthlyLabels);
        model.addAttribute("monthlyValues", monthlyValues);
        model.addAttribute("topMovieLabels", topMovieLabels);
        model.addAttribute("topMovieValues", topMovieValues);
        return "admin";
    }

    @GetMapping("/admin/movies")
    public String adminMoviesPage() {
        return "admin-movies";
    }

    @GetMapping("/admin/showtimes")
    public String adminShowtimesPage() {
        return "admin-showtimes";
    }


    @GetMapping("/admin/staff")
    public String adminStaffPage(Model model) {
        model.addAttribute("staffAccounts", userRepository.findByRole(User.Role.STAFF));
        return "admin-staff";
    }

    @GetMapping("/admin/genres")
    public String adminGenresPage() {
        return "admin-genres";
    }

    @PostMapping("/admin/staff/add")
    public String addStaff(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "Vui lòng nhập username.");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Vui lòng nhập password.");
        } else if (password.length() < 6) {
            errors.put("password", "Password phải từ 6 ký tự.");
        }
        if (userRepository.findByUsername(username.trim()).isPresent()) {
            errors.put("username", "Username đã tồn tại.");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("staffFormErrors", errors);
            model.addAttribute("staffAccounts", userRepository.findByRole(User.Role.STAFF));
            return "admin-staff";
        }

        User user = new User();
        user.setUsername(username.trim());
        String hash = BCryptUtil.hashPassword(password.trim());
        user.setPassword(hash);
        user.setRole(User.Role.STAFF);
        userRepository.save(user);
        return "redirect:/admin/staff";
    }

    @PostMapping("/admin/staff/{id}/update")
    public String updateStaff(@PathVariable Long id,
                              @RequestParam String username,
                              @RequestParam(required = false) String password,
                              Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null || user.getRole() != User.Role.STAFF) {
            return "redirect:/admin/staff";
        }

        String normalizedUsername = username == null ? "" : username.trim();
        if (normalizedUsername.isEmpty()) {
            model.addAttribute("staffUpdateError", "Username không được để trống.");
            model.addAttribute("staffAccounts", userRepository.findByRole(User.Role.STAFF));
            return "admin-staff";
        }

        User duplicatedUser = userRepository.findByUsername(normalizedUsername).orElse(null);
        if (duplicatedUser != null && !duplicatedUser.getId().equals(id)) {
            model.addAttribute("staffUpdateError", "Username đã tồn tại.");
            model.addAttribute("staffAccounts", userRepository.findByRole(User.Role.STAFF));
            return "admin-staff";
        }

        try {
            user.setUsername(normalizedUsername);
            if (password != null && !password.trim().isEmpty()) {
                if (password.trim().length() < 6) {
                    model.addAttribute("staffUpdateError", "Password mới phải từ 6 ký tự.");
                    model.addAttribute("staffAccounts", userRepository.findByRole(User.Role.STAFF));
                    return "admin-staff";
                }
                String hash = BCryptUtil.hashPassword(password.trim());
                user.setPassword(hash);
            }
            userRepository.save(user);
        } catch (IllegalStateException ex) {
            model.addAttribute("staffUpdateError", "Không thể cập nhật tài khoản.");
            model.addAttribute("staffAccounts", userRepository.findByRole(User.Role.STAFF));
            return "admin-staff";
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/admin/staff/{id}/delete")
    public String deleteStaff(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getRole() == User.Role.STAFF) {
            userRepository.delete(user);
        }
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff")
    public String staffPage() {
        return "staff";
    }

    @PostMapping("/login")
    public String doLogin(@Valid @ModelAttribute LoginRequest request, BindingResult bindingResult, HttpSession session, Model model) {
        session.removeAttribute("USER_ID");
        session.removeAttribute("ROLE");

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        String password = request.getPassword() == null ? "" : request.getPassword().trim();

        if (email.isEmpty()) {
            fieldErrors.put("email", "Vui lòng nhập email.");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            fieldErrors.put("email", "Email không đúng định dạng.");
        }
        if (password.isEmpty()) {
            fieldErrors.put("password", "Vui lòng nhập mật khẩu.");
        }

        if (bindingResult.hasFieldErrors("email")) {
            fieldErrors.put("email", bindingResult.getFieldError("email").getDefaultMessage());
        }
        if (bindingResult.hasFieldErrors("password")) {
            fieldErrors.put("password", bindingResult.getFieldError("password").getDefaultMessage());
        }
        if (!fieldErrors.isEmpty()) {
            session.invalidate();
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("email", email);
            return "login";
        }

        try {
            request.setEmail(email);
            request.setPassword(password);
            User user = authService.login(request);
            session.setAttribute("USER_ID", user.getId());
            session.setAttribute("ROLE", user.getRole());
            if (user.getRole() == User.Role.STAFF) {
                return "redirect:/staff";
            }
            if (user.getRole() == User.Role.ADMIN) {
                return "redirect:/admin";
            }
            return "redirect:/home";
        } catch (Exception e) {
            session.invalidate();
            model.addAttribute("formError", "Email hoặc mật khẩu sai, vui lòng nhập lại.");
            model.addAttribute("fieldErrors", new LinkedHashMap<String, String>());
            model.addAttribute("email", email);
            return "login";
        }
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam(required = false) String password,
                             @RequestParam(required = false) String confirmPassword,
                             @RequestParam(required = false) String fullName, @RequestParam(required = false) String email,
                             @RequestParam(required = false) String phone, Model model) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPhone = phone == null ? "" : phone.trim();
        String normalizedFullName = fullName == null ? "" : fullName.trim();
        String normalizedPassword = password == null ? "" : password.trim();
        String normalizedConfirmPassword = confirmPassword == null ? "" : confirmPassword.trim();

        if (normalizedFullName.isEmpty()) {
            fieldErrors.put("fullName", "Vui lòng nhập họ tên.");
        }
        if (normalizedEmail.isEmpty()) {
            fieldErrors.put("email", "Vui lòng nhập email.");
        } else if (!normalizedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            fieldErrors.put("email", "Email không đúng định dạng.");
        } else if (userProfileRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            fieldErrors.put("email", "Email đã tồn tại.");
        }
        if (normalizedPhone.isEmpty()) {
            fieldErrors.put("phone", "Vui lòng nhập số điện thoại.");
        } else if (!normalizedPhone.matches("\\d+")) {
            fieldErrors.put("phone", "Số điện thoại chỉ được chứa chữ số.");
        } else if (userProfileRepository.findByPhone(normalizedPhone).isPresent()) {
            fieldErrors.put("phone", "Số điện thoại đã tồn tại.");
        }
        if (normalizedPassword.isEmpty()) {
            fieldErrors.put("password", "Vui lòng nhập mật khẩu.");
        } else if (normalizedPassword.length() < 6) {
            fieldErrors.put("password", "Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (normalizedConfirmPassword.isEmpty()) {
            fieldErrors.put("confirmPassword", "Vui lòng xác nhận mật khẩu.");
        } else if (!normalizedConfirmPassword.equals(normalizedPassword)) {
            fieldErrors.put("confirmPassword", "Mật khẩu xác nhận không khớp.");
        }

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("fullName", normalizedFullName);
            model.addAttribute("email", normalizedEmail);
            model.addAttribute("phone", normalizedPhone);
            return "register";
        }

        try {
            RegisterRequest request = new RegisterRequest();
            request.setPassword(normalizedPassword);
            request.setConfirmPassword(normalizedConfirmPassword);
            request.setFullName(normalizedFullName);
            request.setEmail(normalizedEmail);
            request.setPhone(normalizedPhone);
            authService.register(request);
            return "redirect:/login";
        } catch (Exception e) {
            String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (message.contains("email")) {
                fieldErrors.put("email", "Email đã tồn tại.");
            }
            if (message.contains("số điện thoại") || message.contains("phone")) {
                fieldErrors.put("phone", "Số điện thoại đã tồn tại.");
            }
            if (message.contains("xác nhận")) {
                fieldErrors.put("confirmPassword", "Mật khẩu xác nhận không khớp.");
            }
            if (message.contains("mật khẩu") && !message.contains("xác nhận")) {
                fieldErrors.put("password", "Vui lòng nhập mật khẩu.");
            }
            if (fieldErrors.isEmpty()) {
                model.addAttribute("formError", "Đăng ký không thành công.");
            } else {
                model.addAttribute("fieldErrors", fieldErrors);
            }
            model.addAttribute("fullName", normalizedFullName);
            model.addAttribute("email", normalizedEmail);
            model.addAttribute("phone", normalizedPhone);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("USER_ID");
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        model.addAttribute("profile", profile);
        model.addAttribute("role", session.getAttribute("ROLE"));
        model.addAttribute("activeTab", "profile");
        model.addAttribute("currentUserName", profile != null ? profile.getFullName() : "bạn");
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) String oldPassword,
                                @RequestParam(required = false) String newPassword,
                                HttpSession session,
                                Model model) {
        Long userId = (Long) session.getAttribute("USER_ID");
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        String normalizedFullName = fullName == null ? "" : fullName.trim();
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPhone = phone == null ? "" : phone.trim();
        String normalizedOldPassword = oldPassword == null ? "" : oldPassword.trim();
        String normalizedNewPassword = newPassword == null ? "" : newPassword.trim();

        if (normalizedFullName.isEmpty()) {
            fieldErrors.put("fullName", "Vui lòng nhập họ tên.");
        }
        if (normalizedEmail.isEmpty()) {
            fieldErrors.put("email", "Vui lòng nhập email.");
        } else if (!normalizedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            fieldErrors.put("email", "Email không đúng định dạng.");
        }
        if (normalizedPhone.isEmpty()) {
            fieldErrors.put("phone", "Vui lòng nhập số điện thoại.");
        }

        if (!normalizedNewPassword.isEmpty()) {
            if (normalizedNewPassword.length() < 6) {
                fieldErrors.put("newPassword", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            }
            if (normalizedOldPassword.isEmpty()) {
                fieldErrors.put("oldPassword", "Vui lòng nhập mật khẩu hiện tại.");
            } else if (user == null) {
                fieldErrors.put("oldPassword", "Không tìm thấy tài khoản người dùng.");
            } else if (!BCryptUtil.checkPassword(normalizedOldPassword, user.getPassword())) {
                fieldErrors.put("oldPassword", "Mật khẩu hiện tại không đúng.");
            }
        }

        if (!fieldErrors.isEmpty()) {
            if (profile == null) {
                profile = new UserProfile();
            }
            profile.setFullName(normalizedFullName);
            profile.setEmail(normalizedEmail);
            profile.setPhone(normalizedPhone);
            model.addAttribute("profile", profile);
            model.addAttribute("role", session.getAttribute("ROLE"));
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("activeTab", "profile");
            return "profile";
        }

        if (user == null) {
            model.addAttribute("errorMessage", "Không tìm thấy tài khoản người dùng.");
        } else if (profile != null) {
            profile.setFullName(normalizedFullName);
            profile.setEmail(normalizedEmail);
            profile.setPhone(normalizedPhone);
            userProfileRepository.save(profile);
            if (!normalizedNewPassword.isEmpty()) {
                String hash = BCryptUtil.hashPassword(normalizedNewPassword);
                user.setPassword(hash);
                userRepository.save(user);
            }
            model.addAttribute("successMessage", "Cập nhật thông tin thành công.");
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy hồ sơ người dùng.");
        }

        model.addAttribute("profile", profile);
        model.addAttribute("role", session.getAttribute("ROLE"));
        model.addAttribute("activeTab", "profile");
        return "profile";
    }
}
