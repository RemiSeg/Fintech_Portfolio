<template>
  <BaseCard>
    <p class="text-xs font-semibold uppercase tracking-wide text-gray-500">
      {{ label }}
    </p>

    <p
      class="numeric mt-3 text-2xl font-semibold"
      :class="valueClass"
    >
      {{ value }}
    </p>

    <p v-if="description" class="mt-1 text-sm text-gray-500">
      {{ description }}
    </p>
  </BaseCard>
</template>

<script setup>
import { computed } from "vue";
import BaseCard from "./BaseCard.vue";

const props = defineProps({
  label: {
    type: String,
    required: true,
  },
  value: {
    type: [String, Number],
    required: true,
  },
  description: {
    type: String,
    default: "",
  },
  tone: {
    type: String,
    default: "neutral",
  },
});

const valueClass = computed(() => {
  if (props.tone === "positive") return "text-[#006c4a]";
  if (props.tone === "negative") return "text-[#ba1a1a]";
  return "text-[#191c1d]";
});
</script>