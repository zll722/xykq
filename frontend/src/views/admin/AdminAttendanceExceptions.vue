<template>
  <div class="attendance-exception-page">
    <section class="page-hero">
      <div>
        <p class="hero-tag">异常处理</p>
        <h1>把异常记录按优先级处理，而不是逐条硬改</h1>
        <p>先看今天哪些异常最值得优先处理，再按状态和日期筛选，最后带着上下文完成修正或转去考勤修正。</p>
      </div>
      <div class="hero-side">
        <strong>{{ summary.total }}</strong>
        <span>条异常待处理</span>
      </div>
    </section>

    <section class="summary-grid">
      <el-card class="summary-card" shadow="hover">
        <span class="summary-label">缺勤</span>
        <strong>{{ summary.absent }}</strong>
        <p>建议优先核对是否漏签或请假未同步</p>
      </el-card>
      <el-card class="summary-card" shadow="hover">
        <span class="summary-label">迟到</span>
        <strong>{{ summary.late }}</strong>
        <p>重点确认是否存在时间窗口或补签争议</p>
      </el-card>
      <el-card class="summary-card" shadow="hover">
        <span class="summary-label">早退</span>
        <strong>{{ summary.earlyLeave }}</strong>
        <p>适合与课程教师反馈联合核查</p>
      </el-card>
    </section>

    <el-card class="filter-card" shadow="hover">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="课程ID">
          <el-input v-model="filters.courseId" placeholder="可选" clearable />
        </el-form-item>
        <el-form-item label="班级ID">
          <el-input v-model="filters.classId" placeholder="可选" clearable />
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="filters.attendanceDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            clearable
          />
        </el-form-item>
        <el-form-item label="异常类型">
          <el-select v-model="filters.status" placeholder="全部异常" clearable>
            <el-option label="缺勤" value="ABSENT" />
            <el-option label="迟到" value="LATE" />
            <el-option label="早退" value="EARLY_LEAVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">刷新异常</el-button>
          <el-button @click="resetFilters">重置筛选</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="title">待处理异常列表</span>
            <p>缺勤优先展示；可以直接在当前页完成修正，也可以跳去考勤修正页做更细处理。</p>
          </div>
        </div>
      </template>

      <el-table :data="sortedList" border stripe v-loading="loading">
        <el-table-column label="优先级" width="96">
          <template #default="{ row }">
            <el-tag :type="row.priorityType">{{ row.priorityText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="课程" prop="courseName" min-width="150" />
        <el-table-column label="班级" prop="className" min-width="170" />
        <el-table-column label="学生ID" prop="studentId" width="96" />
        <el-table-column label="日期" prop="attendanceDate" width="120" />
        <el-table-column label="签到时间" min-width="170">
          <template #default="{ row }">
            {{ row.signedAt || '未签到' }}
          </template>
        </el-table-column>
        <el-table-column label="当前状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.statusTagType">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="建议处理" min-width="220">
          <template #default="{ row }">
            {{ row.suggestion }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <div class="action-group">
              <el-button type="primary" @click="openResolve(row)">立即处理</el-button>
              <el-button @click="goAdjust(row)">去修正页</el-button>
            </div>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="当前筛选条件下没有待处理异常" />
        </template>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" title="处理异常记录" width="560px">
      <el-form label-position="top">
        <el-form-item label="异常记录">
          <el-alert :title="selectedSummary" type="warning" :closable="false" />
        </el-form-item>
        <el-form-item label="调整为">
          <el-select v-model="form.afterStatus" placeholder="请选择处理结果" style="width: 100%">
            <el-option label="正常" value="PRESENT" />
            <el-option label="迟到" value="LATE" />
            <el-option label="缺勤" value="ABSENT" />
            <el-option label="请假" value="LEAVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理原因">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            maxlength="200"
            show-word-limit
            placeholder="例如：学生补交证明、教师确认设备故障导致漏签、审批通过后同步修正"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { listAttendanceExceptionsApi, resolveAttendanceExceptionApi } from '../../api/adminAttendanceManage';

const router = useRouter();
const loading = ref(false);
const visible = ref(false);
const submitting = ref(false);
const selected = ref(null);
const list = ref([]);
const filters = reactive({
  courseId: '',
  classId: '',
  attendanceDate: '',
  status: ''
});
const form = reactive({
  afterStatus: 'PRESENT',
  reason: ''
});

const statusMeta = {
  ABSENT: {
    text: '缺勤',
    priority: 3,
    priorityText: '高',
    priorityType: 'danger',
    tagType: 'danger',
    suggestion: '优先确认是否漏签、请假未同步，或是否需要联系教师核实'
  },
  LATE: {
    text: '迟到',
    priority: 2,
    priorityText: '中',
    priorityType: 'warning',
    tagType: 'warning',
    suggestion: '适合确认签到时间是否合理，必要时修正为正常或保留迟到'
  },
  EARLY_LEAVE: {
    text: '早退',
    priority: 1,
    priorityText: '中',
    priorityType: 'warning',
    tagType: 'info',
    suggestion: '结合课堂实际情况核查是否提前离场，必要时转入人工修正'
  }
};

const selectedSummary = computed(() => {
  if (!selected.value) return '未选择异常记录';
  return `记录 #${selected.value.id} · ${selected.value.courseName || '未命名课程'} · ${selected.value.className || '未命名班级'} · 当前状态 ${selected.value.statusText}`;
});

const sortedList = computed(() =>
  [...list.value].sort((a, b) => {
    if (b.priority !== a.priority) {
      return b.priority - a.priority;
    }
    return String(b.attendanceDate).localeCompare(String(a.attendanceDate));
  })
);

const summary = computed(() => ({
  total: list.value.length,
  absent: list.value.filter((item) => item.status === 'ABSENT').length,
  late: list.value.filter((item) => item.status === 'LATE').length,
  earlyLeave: list.value.filter((item) => item.status === 'EARLY_LEAVE').length
}));

const decorate = (rows) =>
  rows.map((item) => {
    const meta = statusMeta[item.status] || {
      text: item.status,
      priority: 0,
      priorityText: '低',
      priorityType: '',
      tagType: '',
      suggestion: '请结合考勤上下文决定如何处理'
    };
    return {
      ...item,
      statusText: meta.text,
      statusTagType: meta.tagType,
      priority: meta.priority,
      priorityText: meta.priorityText,
      priorityType: meta.priorityType,
      suggestion: meta.suggestion
    };
  });

const loadData = async () => {
  loading.value = true;
  try {
    const params = { ...filters };
    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === null) {
        delete params[key];
      }
    });
    list.value = decorate(await listAttendanceExceptionsApi(params));
  } catch (error) {
    ElMessage.error(error.message || '加载异常列表失败');
  } finally {
    loading.value = false;
  }
};

const resetFilters = () => {
  filters.courseId = '';
  filters.classId = '';
  filters.attendanceDate = '';
  filters.status = '';
  loadData();
};

const openResolve = (row) => {
  selected.value = row;
  form.afterStatus = row.status === 'ABSENT' ? 'LEAVE' : 'PRESENT';
  form.reason = '';
  visible.value = true;
};

const goAdjust = (row) => {
  router.push({
    path: '/admin/attendance-adjustments',
    query: {
      courseId: row.courseId,
      classId: row.classId,
      attendanceDate: row.attendanceDate
    }
  });
};

const submit = async () => {
  if (!selected.value) return;
  submitting.value = true;
  try {
    await resolveAttendanceExceptionApi(selected.value.id, {
      afterStatus: form.afterStatus,
      reason: form.reason
    });
    ElMessage.success('异常记录已处理');
    visible.value = false;
    await loadData();
  } catch (error) {
    ElMessage.error(error.message || '处理失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

onMounted(loadData);
</script>

<style scoped>
.attendance-exception-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #4a1f48 0%, #8b2f59 55%, #d66a4d 100%);
  color: #fff;
}

.hero-tag {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.page-hero h1 {
  margin: 0;
  color: #fff;
}

.page-hero p {
  margin: 10px 0 0;
  max-width: 680px;
  color: rgba(255, 255, 255, 0.82);
}

.hero-side {
  min-width: 180px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.12);
}

.hero-side strong,
.hero-side span {
  display: block;
}

.hero-side strong {
  font-size: 32px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.summary-card {
  border: none;
  border-radius: 22px;
}

.summary-label {
  display: block;
  margin-bottom: 8px;
  color: #7b8794;
}

.summary-card strong {
  font-size: 32px;
  color: #183550;
}

.summary-card p {
  margin: 10px 0 0;
  color: #6d8195;
}

.filter-card,
.table-card {
  border: none;
  border-radius: 24px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 0;
}

.card-header p {
  margin: 6px 0 0;
  color: #7890a8;
}

.title {
  font-size: 18px;
  font-weight: 700;
  color: #17324d;
}

.action-group {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

@media (max-width: 960px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .page-hero {
    flex-direction: column;
  }
}
</style>
