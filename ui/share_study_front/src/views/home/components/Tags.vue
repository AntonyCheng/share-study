<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import { defineComponent, onMounted, ref, nextTick } from "vue";
import { register, getCollegeList } from "@/api/login";
import { CollegeMember } from "@/views/register/index.vue";

export default defineComponent({
  emits: ["getTagNameChild"],
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例

    // dom
    const radios = ref<HTMLElement[]>([]);
    const selectRadio = ref<string>("哈尔滨商业大学");

    // 高校名 + 高校代码
    const collegeList = ref<object[]>([]);
    const testCollegeList = ref<CollegeMember[]>([
      {
        id: "1",
        name: "综合",
        code: "11111",
      },
      {
        id: "1627965942293053441",
        name: "哈尔滨商业大学",
        code: "10240",
      },
      {
        id: "1640264651585437697",
        name: "哈尔滨金融学院",
        code: "10245",
      },
      {
        id: "1640265176313839617",
        name: "黑龙江工程学院",
        code: "13304",
      },
      {
        id: "1640265378022113282",
        name: "齐齐哈尔大学",
        code: "10232",
      },
      {
        id: "1640265023729254401",
        name: "黑河学院",
        code: "13744",
      },
    ]);
    // 生命周期钩子
    onMounted(() => {
      // getCollegeListInfo();
      // handleClick(
      //   {
      //     id: "1627965942293053441",
      //     name: "哈尔滨商业大学",
      //     code: "10240",
      //   },
      //   2
      // );
    });

    // 方法 methods
    const getCollegeListInfo = async () => {
      const { data } = await getCollegeList();
      collegeList.value = data;
    };

    const handleClick = (currentItem: CollegeMember, index: number) => {
      // 单击函数
      // console.log(currentItem);
      // console.log(emits);
      // emits("getTagNameChild", currentItem);
      context.emit("getTagNameChild", currentItem.name);
      selectRadio.value = currentItem.name;
      // nextTick(() => {
      //   console.log(index - 1);
      //   console.log(radios.value[index - 1]);
      //   radios.value[index - 1].checked = true;
      // });
    };

    // 计算方法 computed

    // 监听 watch

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
      collegeList,
      testCollegeList,
      radios,
      selectRadio,

      handleClick,
    };
  },
});
</script>

<template>
  <div class="radio-inputs">
    <label
      class="radio"
      ref="radios"
      v-for="(collegeItem, index) in testCollegeList"
      :key="collegeItem.code"
    >
      <input
        type="radio"
        name="radio"
        :value="collegeItem.name"
        v-model="selectRadio"
        @click="handleClick(collegeItem, index)"
      />
      <span class="name">{{ collegeItem.name }}</span>
    </label>
  </div>
</template>

<style lang="scss">
.radio-inputs {
  // 补充
  // margin: 0 auto;
  // width: 120px;
  flex-direction: column;
  flex-wrap: nowrap;
  overflow-x: auto;
}

.radio-inputs {
  position: relative;
  display: flex;
  // flex-wrap: wrap;
  // border-radius: 0.5rem;
  // background-color: #eee;
  border-radius: 0.2rem;
  background-color: #f9fbff;
  box-sizing: border-box;
  box-shadow: 0 0 0px 1px rgba(0, 0, 0, 0.06);
  padding: 0.25rem;
  // width: 300px;
  font-size: 14px;
}

.radio-inputs .radio {
  // flex: 1 1 auto;
  text-align: center;
}

.radio-inputs .radio input {
  display: none;
}

.radio-inputs .radio .name {
  display: flex;
  cursor: pointer;
  align-items: center;
  justify-content: center;
  // border-radius: 0.5rem;
  border-radius: 0.2rem;
  border: none;
  // padding: 0.5rem 0;
  padding: 10px 17px;
  // color: rgba(51, 65, 85, 1);
  transition: all 0.15s ease-in-out;
}

.name {
  // 补充
  font-size: 18px;
  color: #515767;
}

.radio-inputs .radio input:hover + .name {
  // background-color: #fff;
  background-color: #e9ecef;
  color: #409eff;
}

.radio-inputs .radio input:checked + .name {
  // background-color: #fff;
  background-color: #eaf2ff;
  color: #409eff;
  font-weight: 600;
}
</style>
