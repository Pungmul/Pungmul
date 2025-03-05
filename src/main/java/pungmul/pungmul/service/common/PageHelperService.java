package pungmul.pungmul.service.common;

import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PageHelperService {

    public static <T> PageInfo<T> copyPageInfo(PageInfo<?> originalPageInfo, List<T> newList) {
        // 새로운 PageInfo 객체 생성
        PageInfo<T> newPageInfo = new PageInfo<>(newList);

        // 기존 PageInfo의 상태 값들을 수동으로 복사
        newPageInfo.setPageNum(originalPageInfo.getPageNum());         // 현재 페이지
        newPageInfo.setPageSize(originalPageInfo.getPageSize());       // 페이지 크기
        newPageInfo.setSize(originalPageInfo.getSize());               // 페이지 크기 (size)
        newPageInfo.setStartRow(originalPageInfo.getStartRow());       // 시작 행
        newPageInfo.setEndRow(originalPageInfo.getEndRow());           // 끝 행
        newPageInfo.setPages(originalPageInfo.getPages());             // 총 페이지 수
        newPageInfo.setPrePage(originalPageInfo.getPrePage());         // 이전 페이지
        newPageInfo.setNextPage(originalPageInfo.getNextPage());       // 다음 페이지
        newPageInfo.setIsFirstPage(originalPageInfo.isIsFirstPage());  // 첫 번째 페이지 여부
        newPageInfo.setIsLastPage(originalPageInfo.isIsLastPage());    // 마지막 페이지 여부
        newPageInfo.setHasPreviousPage(originalPageInfo.isHasPreviousPage()); // 이전 페이지 여부
        newPageInfo.setHasNextPage(originalPageInfo.isHasNextPage());       // 다음 페이지 여부
        newPageInfo.setNavigatePages(originalPageInfo.getNavigatePages());  // 페이지 네비게이션 수
        newPageInfo.setNavigatepageNums(originalPageInfo.getNavigatepageNums()); // 네비게이션 페이지 번호
        newPageInfo.setNavigateFirstPage(originalPageInfo.getNavigateFirstPage()); // 첫 번째 페이지 네비게이션
        newPageInfo.setNavigateLastPage(originalPageInfo.getNavigateLastPage());   // 마지막 페이지 네비게이션

        return newPageInfo;
    }

}
