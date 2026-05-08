interface Props {
  className?: string;
  rounded?: string;
}

export default function Skeleton({ className = "h-4 w-full", rounded = "rounded-md" }: Props) {
  return (
    <div
      className={`${className} ${rounded} bg-ancient-rice/60 dark:bg-bg-cardElevated/60 border border-ancient-bronze/20 animate-pulseSoft`}
    />
  );
}
