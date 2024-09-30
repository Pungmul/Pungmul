package pungmul.pungmul.web.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.service.friend.FriendService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/friends")
public class FriendController {
    private final FriendService friendService;

    //  친구 신청
//    @PreAuthorize("hasRole('USER')")
//    @PostMapping("")
//    public ResponseEntity<BaseResponse<Void>> sendFriendRequest(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestParam String receiverUserName
//            ){
//        friendService.sendFriendRequest();
//
//        return
//    }


    //  친구 요청 수락

    //  친구 정보 보기

    //
}
