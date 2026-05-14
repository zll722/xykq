import { expect, test } from '@playwright/test';

function mockApi(route) {
  const req = route.request();
  const method = req.method();
  const url = new URL(req.url());
  const path = url.pathname;

  const ok = (data) =>
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ code: 0, message: 'success', data })
    });

  if (method === 'POST' && path === '/api/v1/auth/login') {
    const body = req.postDataJSON();
    if (body.username === 'student001') {
      return ok({
        token: 't-user',
        userInfo: { id: 2, username: 'student001', realName: '测试学生', roleCode: 'USER' },
        permissions: []
      });
    }
    return ok({
      token: 't-admin',
      userInfo: { id: 1, username: 'admin', realName: '管理员', roleCode: 'ADMIN' },
      permissions: ['leave:approve']
    });
  }

  if (method === 'GET' && path === '/api/v1/schedules/my') {
    return ok([
      {
        scheduleId: 1,
        courseId: 1,
        classId: 1,
        courseName: '程序设计基础',
        weekDay: 1,
        startTime: '08:00:00',
        endTime: '09:40:00',
        location: 'A-101'
      }
    ]);
  }

  if (method === 'POST' && path === '/api/v1/attendance/sign-in') {
    return ok({ status: 'PRESENT', signedAt: '2026-03-13T08:02:00' });
  }

  if (method === 'POST' && path === '/api/v1/leave/requests') {
    return ok({ id: 1001 });
  }

  if (method === 'GET' && path === '/api/v1/leave/requests/my-progress') {
    return ok([
      {
        requestId: 1001,
        status: 'PENDING',
        leaveType: '病假',
        startTime: '2026-03-20 08:00:00',
        endTime: '2026-03-20 18:00:00',
        submittedAt: '2026-03-13 10:00:00'
      }
    ]);
  }

  if (method === 'GET' && path === '/api/v1/leave/approvals/pending') {
    return ok([
      {
        id: 1001,
        studentId: 2,
        leaveType: '病假',
        reason: '发烧',
        startTime: '2026-03-20T08:00:00',
        endTime: '2026-03-20T18:00:00'
      }
    ]);
  }

  if (method === 'POST' && path.startsWith('/api/v1/leave/approvals/')) {
    return ok(null);
  }

  if (method === 'GET' && path === '/api/v1/messages') {
    return ok([]);
  }

  return ok([]);
}

async function login(page, username, password) {
  await page.goto('/login');
  await page.getByTestId('login-username').fill(username);
  await page.getByTestId('login-password').fill(password);
  await page.getByTestId('login-submit').click();
}

test('login page should be responsive in desktop and mobile', async ({ page }) => {
  await page.setViewportSize({ width: 1366, height: 900 });
  await page.goto('/login');
  await page.waitForSelector('.login-layout');

  const desktopColumns = await page.evaluate(
    () => getComputedStyle(document.querySelector('.login-layout')).gridTemplateColumns
  );
  expect(desktopColumns).not.toBe('none');

  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto('/login');
  await page.waitForSelector('.login-layout');

  const mobileColumns = await page.evaluate(
    () => getComputedStyle(document.querySelector('.login-layout')).gridTemplateColumns
  );
  expect(mobileColumns).toBe('390px');

  await expect(page.getByTestId('login-submit')).toBeVisible();
});

test('user flow: login -> sign-in -> leave apply -> progress', async ({ page }) => {
  await page.route('**/api/v1/**', mockApi);

  await login(page, 'student001', '123456');

  await page.goto('/user/sign-in');
  await page.getByTestId('sign-in-submit').first().click();
  await expect(page.getByText(/签到结果：PRESENT/)).toBeVisible();

  await page.goto('/user/leave-apply');
  await page.getByTestId('leave-start-time').fill('2026-03-20 08:00:00');
  await page.getByTestId('leave-end-time').fill('2026-03-20 18:00:00');
  await page.getByTestId('leave-reason').fill('发烧请假去医院看病');
  await page.getByTestId('leave-submit').click();
  await expect(page.getByText(/提交成功/)).toBeVisible();

  await page.goto('/user/leave-progress');
  await expect(page.getByText('PENDING')).toBeVisible();
});

test('admin flow: login -> approve leave', async ({ page }) => {
  await page.route('**/api/v1/**', mockApi);

  await login(page, 'admin', '123456');

  await page.goto('/admin/leave-pending');
  await expect(page.getByText('病假')).toBeVisible();
  await page.getByTestId('admin-approve-1001').click();
});
