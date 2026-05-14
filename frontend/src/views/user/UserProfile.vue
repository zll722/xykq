<template>
  <div class="profile-center-page">
    <section class="profile-hero">
      <div>
        <p class="hero-tag">个人设置</p>
        <h1>维护你的基础信息</h1>
        <p>把常用资料放在一个面板里，头像、姓名、联系方式一次改完，不再分散在多个入口。</p>
      </div>
      <div class="profile-summary">
        <strong>{{ profile.realName || profile.username || '未命名用户' }}</strong>
        <span>{{ profile.username || '账号未加载' }}</span>
      </div>
    </section>

    <el-row :gutter="18">
      <el-col :xs="24" :xl="16">
        <el-card class="profile-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div>
                <span class="title">资料编辑</span>
                <p>用户名只读，其他资料改完后统一保存</p>
              </div>
            </div>
          </template>

          <el-form label-position="top" class="profile-form">
            <el-row :gutter="16">
              <el-col :xs="24" :md="12">
                <el-form-item label="用户名">
                  <el-input :model-value="profile.username" readonly />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="姓名">
                  <el-input v-model="profile.realName" placeholder="请输入真实姓名" name="realName" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :xs="24" :md="12">
                <el-form-item label="手机号">
                  <el-input v-model="profile.phone" placeholder="请输入手机号" name="phone" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="邮箱">
                  <el-input v-model="profile.email" placeholder="请输入邮箱" name="email" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="头像地址">
              <el-input v-model="profile.avatarUrl" placeholder="上传成功后会自动回填" name="avatarUrl" />
            </el-form-item>

            <div class="avatar-upload-row">
              <el-upload
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleFileChange"
                :before-upload="() => false"
              >
                <el-button>选择头像</el-button>
              </el-upload>
              <div class="upload-text">
                <strong>{{ selectedFileName }}</strong>
                <span>支持先选择再上传，成功后自动回填头像地址</span>
              </div>
              <el-button :loading="uploading" :disabled="!selectedFile" @click="uploadAvatar">上传头像</el-button>
            </div>

            <div class="form-actions">
              <el-button type="primary" :loading="saving" @click="save">保存资料</el-button>
            </div>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="8">
        <el-card class="tip-card" shadow="hover">
          <template #header>
            <div class="card-header compact">
              <div>
                <span class="title">填写建议</span>
                <p>便于老师和管理员快速识别你的信息</p>
              </div>
            </div>
          </template>

          <ul class="tip-list">
            <li>姓名建议填写真实姓名，方便审批和签到核对</li>
            <li>手机和邮箱建议至少补齐一项，便于接收通知</li>
            <li>修改后立即生效，建议保存后回到工作台确认显示是否正确</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getMyProfileApi, updateMyProfileApi } from '../../api/user';
import { uploadFileApi } from '../../api/file';

const profile = ref({
  username: '',
  realName: '',
  phone: '',
  email: '',
  avatarUrl: ''
});

const saving = ref(false);
const uploading = ref(false);
const selectedFile = ref(null);

const selectedFileName = computed(() => selectedFile.value?.name || '尚未选择文件');

const loadProfile = async () => {
  try {
    profile.value = await getMyProfileApi();
  } catch (error) {
    console.error(error);
    ElMessage.error('资料加载失败，请稍后重试');
  }
};

const handleFileChange = (file) => {
  selectedFile.value = file.raw;
};

const uploadAvatar = async () => {
  if (!selectedFile.value) return;
  uploading.value = true;
  try {
    const data = await uploadFileApi(selectedFile.value);
    profile.value.avatarUrl = data.url || data.path || '';
    ElMessage.success('头像上传成功');
  } catch (error) {
    console.error(error);
    ElMessage.error('头像上传失败，请稍后重试');
  } finally {
    uploading.value = false;
  }
};

const save = async () => {
  saving.value = true;
  try {
    await updateMyProfileApi(profile.value);
    ElMessage.success('资料已保存');
    loadProfile();
  } catch (error) {
    console.error(error);
    ElMessage.error('保存失败，请稍后重试');
  } finally {
    saving.value = false;
  }
};

onMounted(() => {
  loadProfile();
});
</script>

<style scoped>
.profile-center-page {
  display: grid;
  gap: 18px;
}

.profile-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #143a63 0%, #275f9d 55%, #4ca59f 100%);
  color: #fff;
}

.hero-tag {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.profile-hero h1 {
  margin: 0;
  color: #fff;
}

.profile-hero p {
  max-width: 560px;
  margin: 10px 0 0;
  color: rgba(255, 255, 255, 0.82);
}

.profile-summary {
  min-width: 220px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.12);
}

.profile-summary strong,
.profile-summary span {
  display: block;
}

.profile-summary span {
  margin-top: 8px;
  color: rgba(255, 255, 255, 0.76);
}

.profile-card,
.tip-card {
  border-radius: 24px;
  border: none;
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

.profile-form {
  display: grid;
  gap: 8px;
}

.avatar-upload-row {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 14px;
  align-items: center;
  padding: 14px 16px;
  border-radius: 18px;
  background: linear-gradient(180deg, #f7fbff 0%, #eef4fb 100%);
}

.upload-text strong,
.upload-text span {
  display: block;
}

.upload-text span {
  margin-top: 6px;
  color: #7890a8;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 10px;
}

.tip-list {
  margin: 0;
  padding-left: 18px;
  display: grid;
  gap: 10px;
  color: #5f7893;
}
</style>
