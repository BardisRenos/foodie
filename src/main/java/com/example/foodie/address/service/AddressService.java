package com.example.foodie.address.service;

import com.example.foodie.address.dto.AddressDto;
import com.example.foodie.address.internal.Address;
import com.example.foodie.address.internal.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public List<AddressDto.AddressResponse> findByUser(String userId) {
        return addressRepository.findByUserId(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AddressDto.AddressResponse create(String userId, AddressDto.CreateRequest request) {
        // If new address is default, unset existing default
        if (request.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(a -> {
                        a.setDefault(false);
                        addressRepository.save(a);
                    });
        }
        Address address = new Address();
        address.setUserId(userId);
        address.setFullName(request.fullName());
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setRegion(request.region());
        address.setZipCode(request.zipCode());
        address.setCountry(request.country() != null ? request.country() : "Greece");
        address.setPhone(request.phone());
        address.setDefault(request.isDefault());
        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressDto.AddressResponse setDefault(String userId, String addressId) {
        // Unset current default
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(a -> {
                    a.setDefault(false);
                    addressRepository.save(a);
                });
        // Set new default
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));
        address.setDefault(true);
        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(String userId, String addressId) {
        addressRepository.deleteByUserIdAndId(userId, addressId);
    }

    private AddressDto.AddressResponse toResponse(Address a) {
        return new AddressDto.AddressResponse(
                a.getId(), a.getUserId(), a.getFullName(),
                a.getStreet(), a.getCity(), a.getRegion(),
                a.getZipCode(), a.getCountry(), a.getPhone(),
                a.isDefault()
        );
    }
}