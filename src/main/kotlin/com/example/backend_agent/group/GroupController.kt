package com.example.backend_agent.group

import com.example.backend_agent.bd.model.Group
import com.example.backend_agent.bd.model.GroupAccess
import com.example.backend_agent.bd.repository.AuthRepository
import com.example.backend_agent.bd.repository.GroupAccessRepository
import com.example.backend_agent.bd.repository.GroupRepository
import com.example.utils.component.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// DTO для запроса
data class CreateGroupRequest(
    val groupName: String
)

// DTO для ответа
data class GroupSummary(
    val id: Long?,
    val name: String,
    val ownerName: String
)

// DTO доступ пользователям к моей группе
data class GrantAccessRequest(
    val groupId: Long,
    val phoneNumbers: List<String>
)

@RestController
@RequestMapping("/api/groups")
class GroupController(
    private val groupRepository: GroupRepository,
    private val groupAccessRepository: GroupAccessRepository,
    private val userRepository: AuthRepository
) {
    @PostMapping
    fun createGroup(
        @RequestBody request: CreateGroupRequest,
        @AuthenticationPrincipal userDetails: org.springframework.security.core.userdetails.User
    ): ResponseEntity<ApiResponse<GroupSummary>> {

        val phoneNumber = userDetails.username
        val currentUser = userRepository.findByPhoneNumber(phoneNumber)
            ?: return ResponseEntity.status(401).body(ApiResponse("ERROR", null, "Пользователь не найден"))

        // Проверка: имя группы уже существует у пользователя?
        val existingGroup = groupRepository.findByOwnerAndName(currentUser, request.groupName)
        if (existingGroup != null) {
            return ResponseEntity.status(400).body(
                ApiResponse("ERROR", null, "Группа с таким именем уже существует")
            )
        }

        val group = groupRepository.save(Group(name = request.groupName, owner = currentUser))
        val summary = GroupSummary(group.id, group.name, group.owner.fullName)

        return ResponseEntity.ok(ApiResponse("SUCCESS", summary, null))
    }

    // 1. Получить мои группы (владельца)
    @GetMapping("/owner/groups")
    fun getMyGroups(
        @AuthenticationPrincipal userDetails: org.springframework.security.core.userdetails.User
    ): ResponseEntity<ApiResponse<List<GroupSummary>>> {
        val currentUser = userRepository.findByPhoneNumber(userDetails.username)
            ?: return ResponseEntity.status(401).body(ApiResponse("ERROR", null, "Пользователь не найден"))

        val groups = groupRepository.findByOwner(currentUser)
            .map { GroupSummary(it.id, it.name, it.owner.fullName) }

        return ResponseEntity.ok(ApiResponse("SUCCESS", groups, null))
    }

    // 2. Получить группы, к которым у меня есть доступ (не мои)
    @GetMapping("/access")
    fun getGroupsWithAccess(
        @AuthenticationPrincipal userDetails: org.springframework.security.core.userdetails.User
    ): ResponseEntity<ApiResponse<List<GroupSummary>>> {
        val currentUser = userRepository.findByPhoneNumber(userDetails.username)
            ?: return ResponseEntity.status(401).body(ApiResponse("ERROR", null, "Пользователь не найден"))

        val groupAccesses = groupAccessRepository.findByUser(currentUser)
        val groups = groupAccesses.map {
            GroupSummary(
                it.group.id,
                it.group.name,
                it.group.owner.fullName  // добавляем имя владельца группы
            )
        }

        return ResponseEntity.ok(ApiResponse("SUCCESS", groups, null))
    }

    // 3. Выдать доступ пользователям к моей группе
    @PostMapping("/grant/access")
    fun grantAccess(
        @RequestBody request: GrantAccessRequest,
        @AuthenticationPrincipal userDetails: org.springframework.security.core.userdetails.User
    ): ResponseEntity<ApiResponse<String>> {
        val currentUser = userRepository.findByPhoneNumber(userDetails.username)
            ?: return ResponseEntity.status(401).body(ApiResponse("ERROR", null, "Пользователь не найден"))

        val group = groupRepository.findById(request.groupId)
            .orElse(null) ?: return ResponseEntity.status(404).body(ApiResponse("ERROR", null, "Группа не найдена"))

        if (group.owner.id != currentUser.id) {
            return ResponseEntity.status(403).body(ApiResponse("ERROR", null, "Доступ запрещён"))
        }

        request.phoneNumbers.forEach { phone ->
            // 🔒 Пропускаем владельца, чтобы он не мог сам себе дать доступ
            if (phone == currentUser.phoneNumber) {
                return@forEach
            }

            val user = userRepository.findByPhoneNumber(phone)
            if (user != null) {
                val existingAccess = groupAccessRepository.findByGroupAndUser(group, user)
                if (existingAccess == null) {
                    val access = GroupAccess(group = group, user = user)
                    groupAccessRepository.save(access)
                }
            }
        }

        return ResponseEntity.ok(ApiResponse("SUCCESS", "Доступ выдан", null))
    }
}