<template>
  <div class="leave-center-page">
    <section class="page-hero">
      <div>
        <p class="page-tag">请假中心</p>
        <h1>一次填清楚，结果看得见</h1>
        <p>先确定时间，再说明原因，最后补充材料。提交后会自动进入审批进度，不用在多个菜单里来回切换。</p>
      </div>
      <div class="hero-note">
        <strong>提交流程</strong>
        <span>填写时间 → 说明原因 → 选择材料 → 提交申请</span>
      </div>
    </section>

    <el-row :gutter="18">
      <el-col :xs="24" :xl="16">
        <el-card class="form-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div>
                <span class="card-title">请假申请</span>
                <p>高频信息放前面，尽量减少无意义操作</p>
              </div>
            </div>
          </template>

          <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="leave-form">
            <el-row :gutter="16">
              <el-col :xs="24" :md="12">
                <el-form-item label="请假类型" prop="leaveType">
                  <el-select v-model="form.leaveType" placeholder="请选择请假类型">
                    <el-option label="病假" value="SICK" />
                    <el-option label="事假" value="PERSONAL" />
                    <el-option label="公假" value="PUBLIC" />
                    <el-option label="其他" value="OTHER" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="快捷时间方案">
                  <div class="preset-group">
                    <el-button v-for="preset in presets" :key="preset.label" @click="applyPreset(preset)">{{ preset.label }}</el-button>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :xs="24" :md="12">
                <el-form-item label="开始时间" prop="startTime">
                  <el-date-picker
                    v-model="form.startTime"
                    type="datetime"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="请选择开始时间"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="结束时间" prop="endTime">
                  <el-date-picker
                    v-model="form.endTime"
                    type="datetime"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="请选择结束时间"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="请假原因" prop="reason">
              <el-input
                v-model="form.reason"
                type="textarea"
                :rows="5"
                maxlength="200"
                show-word-limit
                placeholder="建议描述请假原因、影响课程或是否需要补课，方便老师快速判断"
              />
            </el-form-item>

            <el-form-item label="证明材料">
              <div class="upload-shell">
                <el-button @click="openFileChooser">选择文件</el-button>
                <input ref="fileInputRef" type="file" style="display:none" @change="handleFileChange" />
                <div class="upload-meta">
                  <strong>{{ selectedFileName }}</strong>
                  <span>{{ uploadTip }}</span>
                </div>
                <el-button :loading="uploading" :disabled="!selectedFile" @click="uploadAttachment">上传材料</el-button>
              </div>
            </el-form-item>

            <div class="form-actions">
              <el-button type="primary" :loading="submitting" @click="submit">提交申请</el-button>
              <el-button @click="reset">重置</el-button>
            </div>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="8">
        <el-card class="guide-card" shadow="hover">
          <template #header>
            <div class="card-header compact">
              <div>
                <span class="card-title">提交前提醒</span>
                <p>减少被退回的概率</p>
              </div>
            </div>
          </template>

          <ul class="guide-list">
            <li>开始和结束时间尽量准确到课程或请假时段</li>
            <li>病假、公假建议补充证明材料，提高审批效率</li>
            <li>提交成功后会自动跳转到审批进度页查看状态</li>
          </ul>

          <div class="guide-divider"></div>

          <el-button class="full-button" @click="router.push('/user/leave-progress')">查看审批进度</el-button>
          <el-button class="full-button" @click="router.push('/user/leave-history')">查看历史记录</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { applyLeaveApi } from '../../api/leave';
import { uploadFileApi } from '../../api/file';

const router = useRouter();
const formRef = ref();
const fileInputRef = ref();
const submitting = ref(false);
const uploading = ref(false);
const selectedFile = ref(null);

const form = ref({
  leaveType: 'SICK',
  startTime: '',
  endTime: '',
  reason: '',
  attachmentUrl: ''
});

const rules = {
  leaveType: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  reason: [{ required: true, message: '请填写请假原因', trigger: 'blur' }]
};

const presets = [
  {
    label: '今天上午',
    resolve: () => ({ startTime: buildDate(8, 0), endTime: buildDate(12, 0) })
  },
  {
    label: '今天下午',
    resolve: () => ({ startTime: buildDate(13, 30), endTime: buildDate(18, 0) })
  },
  {
    label: '明天全天',
    resolve: () => ({ startTime: buildDate(8, 0, 1), endTime: buildDate(18, 0, 1) })
  }
];

function buildDate(hour, minute, dayOffset = 0) {
  const date = new Date();
  date.setDate(date.getDate() + dayOffset);
  date.setHours(hour, minute, 0, 0);
  const yyyy = date.getFullYear();
  const mm = `${date.getMonth() + 1}`.padStart(2, '0');
  const dd = `${date.getDate()}`.padStart(2, '0');
  const hh = `${date.getHours()}`.padStart(2, '0');
  const mi = `${date.getMinutes()}`.padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:00`;
}

const selectedFileName = computed(() => selectedFile.value?.name || '尚未选择文件');
const uploadTip = computed(() => form.value.attachmentUrl ? '材料已上传，提交时会一并带上链接' : '可选，病假/公假建议上传证明材料');

const applyPreset = (preset) => {
  const result = preset.resolve();
  form.value.startTime = result.startTime;
  form.value.endTime = result.endTime;
};

const openFileChooser = () => {
  fileInputRef.value?.click();
};

const handleFileChange = (e) => {
  selectedFile.value = e.target.files[0] || null;
};

const uploadAttachment = async () => {
  if (!selectedFile.value) return;
  uploading.value = true;
  try {
    const data = await uploadFileApi(selectedFile.value);
    form.value.attachmentUrl = data || '';
    ElMessage.success('材料上传成功');
  } catch (error) {
    console.error(error);
    ElMessage.error('材料上传失败，请稍后重试');
  } finally {
    uploading.value = false;
  }
};

const submit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate();
  submitting.value = true;
  try {
    const { attachmentUrl, ...rest } = form.value;
    await applyLeaveApi({ ...rest, proofUrl: attachmentUrl || null });
    ElMessage.success('申请已提交，正在为你跳转到审批进度');
    router.push('/user/leave-progress');
  } catch (error) {
    console.error(error);
    ElMessage.error(error.message || '提交失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

const reset = () => {
  form.value = {
    leaveType: 'SICK',
    startTime: '',
    endTime: '',
    reason: '',
    attachmentUrl: ''
  };
  selectedFile.value = null;
  formRef.value?.clearValidate();
};
</script>

<style scoped>
.leave-center-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #143a63 0%, #275f9d 55%, #4ca59f 100%);
  color: #fff;
}

.page-tag {
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
  max-width: 580px;
  margin: 10px 0 0;
  color: rgba(255, 255, 255, 0.82);
}

.hero-note {
  width: 240px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.12);
  align-self: flex-start;
}

.hero-note strong,
.hero-note span {
  display: block;
}

.hero-note span {
  margin-top: 8px;
  color: rgba(255, 255, 255, 0.78);
}

.form-card,
.guide-card {
  border-radius: 24px;
  border: none;
}

.card-header p {
  margin: 6px 0 0;
  color: #7890a8;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #17324d;
}

.leave-form {
  display: grid;
  gap: 10px;
}

.preset-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.upload-shell {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 14px;
  align-items: center;
  padding: 14px 16px;
  border-radius: 18px;
  background: linear-gradient(180deg, #f7fbff 0%, #eef4fb 100%);
}

.upload-meta strong,
.upload-meta span {
  display: block;
}

.upload-meta span {
  margin-top: 6px;
  color: #7890a8;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}

.guide-list {
  margin: 0;
  padding-left: 18px;
  color: #5f7893;
  display: grid;
  gap: 10px;
}

.guide-divider {
  height: 1px;
  margin: 18px 0;
  background: #e0ebf5;
}

.full-button {
  width: 100%;
}

.full-button + .full-button {
  margin-top: 10px;
}
</style>
