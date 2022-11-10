package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.domain.mytamin.dto.request.ReportRequest;
import great.job.mytamin.domain.mytamin.dto.response.FeelingRankResponse;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
import great.job.mytamin.domain.mytamin.dto.response.WeeklyMentalReportResponse;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.entity.Report;
import great.job.mytamin.domain.mytamin.repository.ReportRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.util.ReportUtil;
import great.job.mytamin.domain.util.TimeUtil;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static great.job.mytamin.domain.mytamin.enumerate.MentalCondition.validateCode;
import static great.job.mytamin.global.exception.ErrorMap.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TimeUtil timeUtil;
    private final ReportUtil reportUtil;
    private final MytaminService mytaminService;
    private final ReportRepository reportRepository;

    /*
    하루 진단하기
    */
    @Transactional
    public ReportResponse createReport(User user, ReportRequest request) {
        Mytamin mytamin = mytaminService.findMytaminOrNew(user);
        if (mytamin.getReport() != null) throw new MytaminException(REPORT_ALREADY_DONE_ERROR);
        Report newReport = saveNewReport(user, request, mytamin);
        return ReportResponse.of(
                newReport,
                reportUtil.concatFeelingTag(newReport),
                timeUtil.canEditReport(newReport));
    }

    /*
    하루 진단 수정
    */
    @Transactional
    public void updateReport(User user, Long reportId, ReportRequest request) {
        Report report = findReportById(user, reportId);
        canEdit(report);

        report.updateAll(
                validateCode(request.getMentalConditionCode()),
                request.getTag1(),
                request.getTag2(),
                request.getTag3(),
                request.getTodayReport()
        );
        reportRepository.save(report);
    }

    /*
    하루 진단 조회
    */
    @Transactional(readOnly = true)
    public ReportResponse getReport(User user, Long reportId) {
        Report report = findReportById(user, reportId);
        return ReportResponse.of(
                report,
                reportUtil.concatFeelingTag(report),
                timeUtil.canEditReport(report));
    }

    /*
    주간 마음 컨디션
    */
    @Transactional(readOnly = true)
    public List<WeeklyMentalReportResponse> getWeeklyMentalReport(User user) {
        LocalDateTime start = LocalDateTime.now().minusDays(7); // 오늘을 기준으로 7일 전
        List<WeeklyMentalReportResponse> weeklyMentalReportResponseList = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            LocalDateTime target = start.plusDays(i);
            Mytamin mytamin = mytaminService.findMytamin(user, target);

            weeklyMentalReportResponseList.add(WeeklyMentalReportResponse.of(
                            timeUtil.convertDayNumToStr(target.getDayOfWeek().getValue()),
                            mytamin != null && mytamin.getReport() != null ? mytamin.getReport().getMentalConditionCode() : 0
                    )
            );
        }

        return weeklyMentalReportResponseList;
    }

    /*
    이번 달 가장 많이 느낀 감정
    */
    @Transactional(readOnly = true)
    public List<FeelingRankResponse> getMonthlyFeelingRank(User user) {
        List<String> feelingList = getMontlyTagList(user);
        List<FeelingRankResponse> feelingRankResponseList = countByFeeling(feelingList);
        feelingRankResponseList.sort(Comparator.comparingInt(FeelingRankResponse::getCount)); // count 순 정렬
        Collections.reverse(feelingRankResponseList); // 내림차순
        if (feelingRankResponseList.size() > 3) return feelingRankResponseList.subList(0, 3); // Top3만 리턴
        else return feelingRankResponseList;
    }

    /*
    하루 진단 전체 삭제
    */
    @Transactional
    public void deleteAll(User user) {
        List<Report> reportList = reportRepository.findAllByUser(user);
        for (Report report : reportList) {
            Mytamin mytamin = report.getMytamin();
            mytamin.updateReport(null);  // Mytamin과 연관관계 끊기
            reportRepository.delete(report);

            // 마이타민과 연관된 데이터가 하나도 없다면 -> 마이타민도 삭제
            if (mytamin.getCare() == null) mytaminService.deleteMytamin(user, mytamin.getMytaminId());
        }
    }

    private Report findReportById(User user, Long reportId) {
        return reportRepository.findByUserAndReportId(user, reportId)
                .orElseThrow(() -> new MytaminException(REPORT_NOT_FOUND_ERROR));
    }

    private void canEdit(Report report) {
        if (!timeUtil.isInRange(LocalDateTime.now(), report.getCreatedAt(), report.getCreatedAt().plusDays(1))) {
            throw new MytaminException(EDIT_TIMEOUT_ERROR);
        }
    }

    private Report saveNewReport(User user, ReportRequest request, Mytamin mytamin) {
        Report report = new Report(
                user,
                validateCode(request.getMentalConditionCode()),
                request.getTag1(),
                request.getTag2(),
                request.getTag3(),
                request.getTodayReport(),
                mytamin
        );
        Report newReport = reportRepository.save(report);
        mytamin.updateReport(newReport);
        return newReport;
    }

    private List<String> getMontlyTagList(User user) {
        List<Report> monthlyReportList = getMonthlyReportList(user, LocalDateTime.now());
        List<String> tagList = new ArrayList<>();
        tagList.addAll(monthlyReportList.stream().map(Report::getTag1).collect(Collectors.toList()));
        tagList.addAll(monthlyReportList.stream().map(Report::getTag2).collect(Collectors.toList()));
        tagList.addAll(monthlyReportList.stream().map(Report::getTag3).collect(Collectors.toList()));
        tagList.removeAll(Collections.singletonList(null));
        return tagList;
    }

    private List<Report> getMonthlyReportList(User user, LocalDateTime target) {
        LocalDateTime start = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), 1, 0, 0);
        LocalDateTime end = timeUtil.getLastDayOfMonth(target);
        return reportRepository.findAllByUserAndTakeAtBetween(user, start, end);
    }

    private List<FeelingRankResponse> countByFeeling(List<String> feelingList) {
        Set<String> feelingSet = new HashSet<>(feelingList);
        List<FeelingRankResponse> feelingRankResponseList = new ArrayList<>();
        for (String feeling : feelingSet) {
            feelingRankResponseList.add(FeelingRankResponse.of(
                    feeling,
                    Collections.frequency(feelingList, feeling)
            ));
        }
        return feelingRankResponseList;
    }

}
