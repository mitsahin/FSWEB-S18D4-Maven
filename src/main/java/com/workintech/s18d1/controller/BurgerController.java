package com.workintech.s18d1.controller;

import com.workintech.s18d1.dao.BurgerDao;
import com.workintech.s18d1.entity.BreadType;
import com.workintech.s18d1.entity.Burger;
import com.workintech.s18d1.exceptions.BurgerException;
import com.workintech.s18d1.util.BurgerValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping({"/burger", "/workintech/burgers"})
public class BurgerController {

    private final BurgerDao burgerDao;

    public BurgerController(BurgerDao burgerDao) {
        this.burgerDao = burgerDao;
    }

    @GetMapping
    public List<Burger> findAll() {
        try {
            return burgerDao.findAll();
        } catch (RuntimeException exception) {
            log.error("Error while fetching all burgers", exception);
            throw exception;
        }
    }

    @GetMapping("/{id}")
    public Burger findById(@PathVariable Long id) {
        try {
            BurgerValidation.validateId(id);
            return burgerDao.findById(id);
        } catch (RuntimeException exception) {
            log.error("Error while fetching burger with id {}", id, exception);
            throw exception;
        }
    }

    @PostMapping
    public Burger save(@RequestBody Burger burger) {
        try {
            BurgerValidation.validateBurger(burger);
            return burgerDao.save(burger);
        } catch (RuntimeException exception) {
            log.error("Error while saving burger", exception);
            throw exception;
        }
    }

    @PutMapping({"", "/{id}"})
    public Burger update(@PathVariable(required = false) Long id, @RequestBody Burger burger) {
        try {
            if (burger == null) {
                throw new BurgerException("Burger data cannot be null", HttpStatus.BAD_REQUEST);
            }
            if (id != null) {
                burger.setId(id);
            }
            BurgerValidation.validateId(burger.getId());
            return burgerDao.update(burger);
        } catch (RuntimeException exception) {
            log.error("Error while updating burger with id {}", id != null ? id : burger != null ? burger.getId() : null, exception);
            throw exception;
        }
    }

    @DeleteMapping("/{id}")
    public Burger remove(@PathVariable Long id) {
        try {
            BurgerValidation.validateId(id);
            return burgerDao.remove(id);
        } catch (RuntimeException exception) {
            log.error("Error while removing burger with id {}", id, exception);
            throw exception;
        }
    }

    @GetMapping("/price/{price}")
    public List<Burger> findByPrice(@PathVariable int price) {
        try {
            validatePrice(price);
            return burgerDao.findByPrice(price);
        } catch (RuntimeException exception) {
            log.error("Error while filtering burgers by price {}", price, exception);
            throw exception;
        }
    }

    @GetMapping("/findByPrice")
    public List<Burger> findByPriceFromBody(@RequestBody Map<String, Integer> payload) {
        try {
            Integer price = payload.get("price");
            if (price == null) {
                throw new BurgerException("Price is required", HttpStatus.BAD_REQUEST);
            }
            validatePrice(price);
            return burgerDao.findByPrice(price);
        } catch (RuntimeException exception) {
            log.error("Error while filtering burgers by request body price", exception);
            throw exception;
        }
    }

    @GetMapping("/breadType/{breadType}")
    public List<Burger> findByBreadType(@PathVariable BreadType breadType) {
        try {
            return burgerDao.findByBreadType(breadType);
        } catch (RuntimeException exception) {
            log.error("Error while filtering burgers by bread type {}", breadType, exception);
            throw exception;
        }
    }

    @GetMapping("/findByBreadType")
    public List<Burger> findByBreadTypeFromBody(@RequestBody Map<String, String> payload) {
        try {
            String breadType = payload.get("breadType");
            if (breadType == null || breadType.isBlank()) {
                throw new BurgerException("Bread type is required", HttpStatus.BAD_REQUEST);
            }
            return burgerDao.findByBreadType(BreadType.valueOf(breadType.toUpperCase()));
        } catch (IllegalArgumentException exception) {
            throw new BurgerException("Invalid bread type", HttpStatus.BAD_REQUEST);
        } catch (RuntimeException exception) {
            log.error("Error while filtering burgers by request body bread type", exception);
            throw exception;
        }
    }

    @GetMapping("/content/{content}")
    public List<Burger> findByContent(@PathVariable String content) {
        try {
            BurgerValidation.validateContent(content);
            return burgerDao.findByContent(content);
        } catch (RuntimeException exception) {
            log.error("Error while filtering burgers by content {}", content, exception);
            throw exception;
        }
    }

    @GetMapping("/findByContent")
    public List<Burger> findByContentFromBody(@RequestBody Map<String, String> payload) {
        try {
            String content = payload.get("content");
            BurgerValidation.validateContent(content);
            return burgerDao.findByContent(content);
        } catch (RuntimeException exception) {
            log.error("Error while filtering burgers by request body content", exception);
            throw exception;
        }
    }

    private void validatePrice(Integer price) {
        if (price == null || price <= 0) {
            throw new BurgerException("Price must be greater than zero", HttpStatus.BAD_REQUEST);
        }
    }
}
