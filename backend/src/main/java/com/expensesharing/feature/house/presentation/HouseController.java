package com.expensesharing.feature.house.presentation;

import com.expensesharing.common.response.ApiResponse;
import com.expensesharing.common.support.UsernameEnricher;
import com.expensesharing.feature.house.application.dto.*;
import com.expensesharing.feature.house.application.usecase.*;
import com.expensesharing.feature.house.presentation.request.*;
import com.expensesharing.feature.house.presentation.response.HouseMemberResponse;
import com.expensesharing.feature.house.presentation.response.HouseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/houses")
@RequiredArgsConstructor
public class HouseController {

    private final CreateHouseUseCase createHouseUseCase;
    private final GetHouseUseCase getHouseUseCase;
    private final GetHouseMembersUseCase getHouseMembersUseCase;
    private final UpdateHouseUseCase updateHouseUseCase;
    private final DeleteHouseUseCase deleteHouseUseCase;
    private final InviteMemberUseCase inviteMemberUseCase;
    private final ChangeMemberRoleUseCase changeMemberRoleUseCase;
    private final RemoveMemberUseCase removeMemberUseCase;
    private final LeaveHouseUseCase leaveHouseUseCase;
    private final UsernameEnricher usernameEnricher;

    @PostMapping
    public ResponseEntity<ApiResponse<HouseResponse>> create(
            @Valid @RequestBody CreateHouseRequest request,
            @AuthenticationPrincipal UUID userId) {
        var house = createHouseUseCase.execute(
                new CreateHouseCommand(request.name(), request.description(), userId));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(HouseResponse.from(house), "House created successfully."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HouseResponse>>> getMyHouses(
            @AuthenticationPrincipal UUID userId) {
        List<HouseResponse> houses = getHouseUseCase.getMyHouses(userId)
                .stream().map(HouseResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(houses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HouseResponse>> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        var house = getHouseUseCase.getById(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(HouseResponse.from(house)));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<HouseMemberResponse>>> getMembers(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        List<HouseMemberResponse> members = usernameEnricher.toMemberResponses(
                getHouseMembersUseCase.execute(id, userId));
        return ResponseEntity.ok(ApiResponse.ok(members));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HouseResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateHouseRequest request,
            @AuthenticationPrincipal UUID userId) {
        var house = updateHouseUseCase.execute(
                new UpdateHouseCommand(id, userId, request.name(), request.description()));
        return ResponseEntity.ok(ApiResponse.ok(HouseResponse.from(house)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        deleteHouseUseCase.execute(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "House deleted successfully."));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<HouseMemberResponse>> inviteMember(
            @PathVariable UUID id,
            @Valid @RequestBody InviteMemberRequest request,
            @AuthenticationPrincipal UUID userId) {
        var member = inviteMemberUseCase.execute(
                new InviteMemberCommand(id, userId, request.identifier(), request.role()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(usernameEnricher.toMemberResponse(member), "Member invited successfully."));
    }

    @PutMapping("/{id}/members/{targetUserId}/role")
    public ResponseEntity<ApiResponse<HouseMemberResponse>> changeMemberRole(
            @PathVariable UUID id,
            @PathVariable UUID targetUserId,
            @Valid @RequestBody ChangeMemberRoleRequest request,
            @AuthenticationPrincipal UUID userId) {
        var member = changeMemberRoleUseCase.execute(
                new ChangeMemberRoleCommand(id, userId, targetUserId, request.role()));
        return ResponseEntity.ok(ApiResponse.ok(usernameEnricher.toMemberResponse(member)));
    }

    @DeleteMapping("/{id}/members/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID targetUserId,
            @AuthenticationPrincipal UUID userId) {
        removeMemberUseCase.execute(id, userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Member removed successfully."));
    }

    @DeleteMapping("/{id}/members/me")
    public ResponseEntity<ApiResponse<Void>> leaveHouse(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        leaveHouseUseCase.execute(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Left house successfully."));
    }
}
