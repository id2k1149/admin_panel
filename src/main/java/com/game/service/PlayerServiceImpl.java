package com.game.service;

import com.game.entity.*;
import com.game.repository.PlayerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import static java.lang.Math.sqrt;
import java.util.Date;

@Service
public class PlayerServiceImpl implements PlayerService {

    final
    PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Page<Player> getAll(Specification<Player> specification, Pageable pageable) {
        return playerRepository.findAll(specification, pageable);
    }

    @Override
    public Player create(Player player) {

        checkName(player);
        checkTitle(player);
        checkBirthday(player);
        checkExperience(player);

        Integer level = calculateLevel(player);
        player.setLevel(level);

        Integer untilNextLevel = calculateUntilNextLevel(player);
        player.setUntilNextLevel(untilNextLevel);

        return playerRepository.saveAndFlush(player);
    }

    @Override
    public void delete(Long id) {
        checkId(id);
        playerRepository.deleteById(id);
    }

    @Override
    public Player getById(Long id) {
        checkId(id);
        return playerRepository.findById(id).orElse(null);
    }

    @Override
    public Player update(Long id, Player player) {
        Player playerToEdit = getById(id);

        String name = player.getName();
        if (name != null) {
            checkName(player);
            playerToEdit.setName(name);
        }

        String title = player.getTitle();
        if (title != null) {
            checkTitle(player);
            playerToEdit.setTitle(title);
        }

        Race race = player.getRace();
        if (race != null) {
            playerToEdit.setRace(race);
        }

        Profession profession = player.getProfession();
        if (profession != null) {
            playerToEdit.setProfession(profession);
        }

        Date birthday = player.getBirthday();
        if (birthday != null) {
            checkBirthday(player);
            playerToEdit.setBirthday(birthday);
        }

        Boolean banned = player.getBanned();
        if (banned != null) {
            playerToEdit.setBanned(banned);
        }

        Integer experience = player.getExperience();
        if (experience != null) {
            checkExperience(player);
            playerToEdit.setExperience(experience);

            Integer newLevel = calculateLevel(player);
            playerToEdit.setLevel(newLevel);

            Integer newUntilNextLevel = calculateUntilNextLevel(playerToEdit);
            playerToEdit.setUntilNextLevel(newUntilNextLevel);
        }
        return playerRepository.saveAndFlush(playerToEdit);
    }

    @Override
    public Specification<Player> filterByName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) {
                return null;
            }
            return criteriaBuilder.like(root.get("name"), "%" + name + "%");
        };
    }

    @Override
    public Specification<Player> filterByTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null) {
                return null;
            }
            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        };
    }

    @Override
    public Specification<Player> filterByRace(Race race) {
        return (root, query, criteriaBuilder) -> {
            if (race == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("race"), race);
        };
    }

    @Override
    public Specification<Player> filterByProfession(Profession profession) {
        return (root, query, criteriaBuilder) -> {
            if (profession == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("profession"), profession);
        };
    }

    @Override
    public Specification<Player> filterByBirthday(Long after, Long before) {

        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) {
                return null;
            }

            if (after == null) {
                Date birthdayBefore = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), birthdayBefore);
            }

            if (before == null) {
                Date birthdayAfter = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), birthdayAfter);
            }

            Date birthdayAfter = new Date(after);
            Date birthdayBefore = new Date(before);
            return criteriaBuilder.between(root.get("birthday"), birthdayAfter, birthdayBefore);
        };
    }

    @Override
    public Specification<Player> filterByBanned(Boolean banned) {
        return (root, query, criteriaBuilder) -> {
            if (banned == null) {
                return null;
            }
            if (banned) {
                return criteriaBuilder.isTrue(root.get("banned"));
            } else {
                return criteriaBuilder.isFalse(root.get("banned"));
            }
        };
    }

    @Override
    public Specification<Player> filterByExperience(Integer minExperience, Integer maxExperience) {
        return (root, query, criteriaBuilder) -> {
            if (minExperience == null && maxExperience == null) {
                return null;
            }
            if (minExperience == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExperience);
            }
            if (maxExperience == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExperience);
            }
            return criteriaBuilder.between(root.get("experience"), minExperience, maxExperience);
        };
    }

    @Override
    public Specification<Player> filterByLevel(Integer minLevel, Integer maxLevel) {
        return (root, query, criteriaBuilder) -> {
            if (minLevel == null && maxLevel == null) {
                return null;
            }
            if (minLevel == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel);
            }
            if (maxLevel == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel);
            }
            return criteriaBuilder.between(root.get("level"), minLevel, maxLevel);
        };
    }

    private void checkId(Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!playerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private Integer calculateLevel(Player player) {
        double result = (sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
        return (int) result;
    }

    private Integer calculateUntilNextLevel(Player player) {
        Integer lvl = player.getLevel();
        return 50 * (lvl + 1) * (lvl + 2) - player.getExperience();
    }

    private void checkName(Player player) {
        String name = player.getName();
        if (name.length() > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void checkTitle(Player player) {
        String title = player.getTitle();
        if (title.length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void checkBirthday(Player player) {
        Date birthday = player.getBirthday();
        if ((birthday.getYear() + 1900) < 2000 || (birthday.getYear() + 1900)  > 3000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void checkExperience(Player player) {
        Integer experience = player.getExperience();
        if (experience < 0 || experience > 10000000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}