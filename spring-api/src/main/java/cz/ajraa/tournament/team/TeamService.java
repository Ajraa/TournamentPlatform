package cz.ajraa.tournament.team;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
}
