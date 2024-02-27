package com.jinwoo.pass.passbatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("job")
@RequiredArgsConstructor
public class JobLauncherController {
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @PostMapping("/launcher")
    public ExitStatus launcherJob(@RequestBody JobLauncherRequest request) throws Exception {
        Job job = jobRegistry.getJob(request.getName());
        return this.jobLauncher.run(job, request.getJobParameters()).getExitStatus();
    }
}
