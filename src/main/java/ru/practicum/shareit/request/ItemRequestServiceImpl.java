package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ItemRequestNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createRequest(Integer userId, CreateItemRequestDto createItemRequestDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(createItemRequestDto.getDescription());
        itemRequest.setRequestor(userOptional.get());
        ItemRequest createItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.itemRequestDto(createItemRequest);
    }

    @Override
    public List<ItemRequestDtoWithItem> findAllRequest(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(userId);
        if (itemRequestList.size() == 0) {
            return new ArrayList<>();
        }
        Set<Integer> requestIdList = new HashSet<>();
        for (ItemRequest itemRequest : itemRequestList) {
            requestIdList.add(itemRequest.getId());
        }
        List<Item> itemList = itemRepository.findItemByRequestIdIn(requestIdList);
        List<ItemRequestDtoWithItem> itemRequestDtoList = ItemRequestMapper.toItemRequestDtoWithItemList(itemRequestList, itemList);
        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDtoWithItem findRequestById(Integer userId, Integer requestId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
        if (itemRequestOptional.isEmpty()) {
            throw new ItemRequestNotFoundException(String.format("Запрос с идентификатором %d не существует", requestId));
        }
        ItemRequest itemRequest = itemRequestOptional.get();
        List<Item> itemList = itemRepository.findItemByRequestId(requestId);
        ItemRequestDtoWithItem itemRequestDtoWithItem = ItemRequestMapper.toItemRequestDtoWithItem(itemRequest, itemList);
        return itemRequestDtoWithItem;
    }

    @Override
    public List<ItemRequestDtoWithItem> findAllRequestWith(Integer userId, Integer from, Integer size) {
        Pageable pageable = pageValid(from, size, Sort.by(Sort.Direction.ASC, "created"));
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
        List<ItemRequest> itemRequestList = itemRequestRepository.findByRequestorIdNot(userId, pageable);
        Set<Integer> requestIdList = new HashSet<>();
        for (ItemRequest itemRequest : itemRequestList) {
            requestIdList.add(itemRequest.getId());
        }
        List<Item> itemList = itemRepository.findItemByRequestIdIn(requestIdList);
        List<ItemRequestDtoWithItem> itemRequestDtoList = ItemRequestMapper.toItemRequestDtoWithItemList(itemRequestList, itemList);
        return itemRequestDtoList;
    }

    private Pageable pageValid(Integer from, Integer size, Sort sort) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Не верный формат запроса");
        }
        int page = from / size;
        return PageRequest.of(page, size, sort);
    }
}
